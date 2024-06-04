package cruise.promotions

import scala.annotation.tailrec

case class Promotion(code: String, notCombinableWith: Seq[String])

case class PromotionCombo(promotionCodes: Seq[String])

/**
 * Transforms the sequential promotion data into a map representing nodes and adjacent nodes (i.e., promotion
 * codes and combinable promotion codes)
 */
def createPromotionGraph(allPromotions: Seq[Promotion]): Map[String, Set[String]] = {
  val allPromotionCodes = allPromotions.map(_.code).toSet

  val graph = allPromotions.map { promo =>
    promo.code -> (allPromotionCodes -- promo.notCombinableWith - promo.code)
  }.toMap

  return graph
}

case class IntermediateCombo(promotionCodes: Set[String], combinableWith: Set[String])

@tailrec
def findCombinationsFor(
  promotionGraph: Map[String, Set[String]],
  frontier: Seq[IntermediateCombo], // Combinations to explore
  exploredCombinations: Set[Set[String]],
  maximalCombinations: Seq[PromotionCombo]
): Seq[PromotionCombo] = {
  frontier match
    // Process the current combination if it has not already been explored
    case current :: remainingFrontier if !exploredCombinations.contains(current.promotionCodes) => {
      // Find new (potentially intermediate) combinations by adding each available additional promotion in turn
      val newFrontier = current.combinableWith.map { additionalPromotion =>
        val promotionCodes = current.promotionCodes + additionalPromotion
        val combinableWith =
          current.combinableWith & promotionGraph.getOrElse(additionalPromotion, Set.empty)

        IntermediateCombo(promotionCodes, combinableWith)
      }

      // If no more promotions can be added, include this combination in the list of finalized combinations
      val isMaximal = newFrontier.size == 0
      val updatedMaximalCombinations = if (isMaximal) {
        maximalCombinations :+ PromotionCombo(current.promotionCodes.toSeq)
      } else maximalCombinations

      findCombinationsFor(
        promotionGraph,
        remainingFrontier ++ newFrontier,
        exploredCombinations + current.promotionCodes,
        updatedMaximalCombinations
      )
    }

    // Skip the current combination if it has already been explored
    case _ :: remainingFrontier =>
      findCombinationsFor(promotionGraph, remainingFrontier, exploredCombinations, maximalCombinations)

    // Stop searching when all possible combinations have been explored (i.e., frontier is empty)
    case Nil => maximalCombinations

}

/**
 * Finds all `PromotionCombo`s with maximum number of combinable promotions in each.
 */
def allCombinablePromotions(
  allPromotions: Seq[Promotion]
): Seq[PromotionCombo] = {
  val promotionGraph = createPromotionGraph(allPromotions)

  // Create an initial frontier of single-promotion "combinations" for each promotion
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
 * Finds all `PromotionCombo`s for a given promotion from given list of `Promotion`s.
 */
def combinablePromotions(
  promotionCode: String,
  allPromotions: Seq[Promotion]
): Seq[PromotionCombo] = {
  val promotionGraph = createPromotionGraph(allPromotions)

  // Create an initial frontier containing only the specified promotion so only combinations
  // that include this code are considered.
  val combinableWith = promotionGraph.getOrElse(promotionCode, Set.empty)
  val initialFrontier = Seq(IntermediateCombo(Set(promotionCode), combinableWith))

  findCombinationsFor(
    promotionGraph,
    initialFrontier,
    Set.empty,
    Seq.empty
  )
}
