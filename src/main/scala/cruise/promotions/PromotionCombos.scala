package cruise.promotions

import scala.annotation.tailrec

// Background:​ Cruise bookings can have one or more Promotions applied to them.
// But sometimes a Promotion cannot be combined with another Promotion. Our
// application has to find out all possible Promotion Combinations that can be
// applied together.

case class Promotion(code: String, notCombinableWith: Seq[String])

case class PromotionCombo(promotionCodes: Seq[String])

/**
 * Transforms the sequential promotion data into a map representing nodes and adjacent nodes (i.e., promotion
 * codes and combinable promotions)
 */
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
  case current :: remainingFrontier => {
    if (exploredCombinations.contains(current.promotionCodes)) {
      findCombinationsFor(promotionGraph, remainingFrontier, exploredCombinations, combinations)
    } else {
      // Find new (potentially intermediate) combinations by adding each available additional promotion in turn
      val newFrontier = current.combinableWith.map { additionalPromotion =>
        val promotionCodes = current.promotionCodes + additionalPromotion
        val combinableWith = current.combinableWith & promotionGraph.getOrElse(additionalPromotion, Set.empty)

        IntermediateCombo(promotionCodes, combinableWith)
      }

      // If no more promotions can be added, include in the finalized list of combinations
      val isLeaf = newFrontier.size == 0
      val updatedCombinations = if (isLeaf) {
        combinations :+ PromotionCombo(current.promotionCodes.toSeq)
      } else combinations

      findCombinationsFor(
        promotionGraph,
        remainingFrontier ++ newFrontier,
        exploredCombinations + current.promotionCodes,
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
  val promotionGraph = createPromotionGraph(allPromotions)

  // Create an initial frontier of singleton "combinations" for each promotion
  val allPromotionCodes = allPromotions.map(_.code).toSet
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
  val promotionGraph = createPromotionGraph(allPromotions)

  // Create an initial frontier containing only the specified promotion so only combinations that include this code
  // are considered.
  // NOTE: Fails silently when `promotionCode` is not in `allPromotions`
  val combinableWith = promotionGraph.getOrElse(promotionCode, Set.empty)
  val initialFrontier = Seq(IntermediateCombo(Set(promotionCode), combinableWith))

  findCombinationsFor(
    promotionGraph,
    initialFrontier,
    Set.empty,
    Seq.empty
  )
}
