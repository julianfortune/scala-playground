package cruise.promotions

import scala.annotation.tailrec

// Background:​ Cruise bookings can have one or more Promotions applied to them.
// But sometimes a Promotion cannot be combined with another Promotion. Our
// application has to find out all possible Promotion Combinations that can be
// applied together.

case class Promotion(code: String, notCombinableWith: Seq[String])

case class PromotionCombo(promotionCodes: Seq[String])

def createPromotionGraph(allPromotions: Seq[Promotion]): Map[String, Set[String]] = {
  val allPromotionCodes = allPromotions.map(_.code).toSet

  val graph: Map[String, Set[String]] = (for {
    promo <- allPromotions
  } yield {
    promo.code -> (allPromotionCodes -- promo.notCombinableWith - promo.code)
  }).toMap

  return graph
}

case class IntermediateCombo(promotionCodes: Set[String], combinableWith: Set[String])

@tailrec
def findCombinationsFor(
  promotionGraph: Map[String, Set[String]],
  frontier: Seq[IntermediateCombo],
  exploredCombinations: Set[Set[String]],
  combinations: Seq[PromotionCombo]
): Seq[PromotionCombo] = frontier match
  case Nil => combinations
  case currentIntermediateCombo :: remainingFrontier => {
    if (exploredCombinations.contains(currentIntermediateCombo.promotionCodes)) {
      findCombinationsFor(promotionGraph, remainingFrontier, exploredCombinations, combinations)
    } else {
      val newIntermediateCombos = currentIntermediateCombo.combinableWith.map { additionalPromotion =>
        IntermediateCombo(
          currentIntermediateCombo.promotionCodes + additionalPromotion,
          currentIntermediateCombo.combinableWith & promotionGraph
            .get(additionalPromotion)
            .getOrElse(Set.empty)
        )
      }
      val isLeaf = newIntermediateCombos.size == 0

      val updatedCombinations = if (isLeaf) {
        combinations :+ PromotionCombo(currentIntermediateCombo.promotionCodes.toSeq)
      } else combinations

      findCombinationsFor(
        promotionGraph,
        remainingFrontier ++ newIntermediateCombos,
        exploredCombinations + currentIntermediateCombo.promotionCodes,
        updatedCombinations
      )
    }
  }

/**
 * Finds all `PromotionCombo`s with maximum number of combinable promotions in each.
 */
def allCombinablePromotions(
  allPromotions: Seq[Promotion]
): Seq[PromotionCombo] = {
  val allPromotionCodes = allPromotions.map(_.code).toSet
  val promotionGraph = createPromotionGraph(allPromotions)

  val initialFrontier = allPromotions.map { p =>
    val combinableWith = allPromotionCodes -- p.notCombinableWith - p.code
    IntermediateCombo(Set(p.code), combinableWith)
  }

  findCombinationsFor(
    promotionGraph,
    initialFrontier,
    Set.empty,
    Seq.empty
  )
}

/**
 * Finds all PromotionCombos for a given Promotion from given list of Promotions.
 */
def combinablePromotions(
  promotionCode: String,
  allPromotions: Seq[Promotion]
): Seq[PromotionCombo] = {
  val allPromotionCodes = allPromotions.map(_.code).toSet
  val promotionGraph = createPromotionGraph(allPromotions)

  val combinableWith = promotionGraph.get(promotionCode).getOrElse(Set.empty)
  val initialFrontier = Seq(IntermediateCombo(Set(promotionCode), combinableWith))

  findCombinationsFor(
    promotionGraph,
    initialFrontier,
    Set.empty,
    Seq.empty
  )
}
