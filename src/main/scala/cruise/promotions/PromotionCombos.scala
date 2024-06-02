package cruise.promotions

// Background:​ Cruise bookings can have one or more Promotions applied to them.
// But sometimes a Promotion cannot be combined with another Promotion. Our
// application has to find out all possible Promotion Combinations that can be
// applied together.


case class Promotion(code: String, notCombinableWith: Seq[String])

case class PromotionCombo(promotionCodes: Seq[String])


/**
  * Implement a function to find all PromotionCombos with maximum number of
  * combinable promotions in each. The function and case class definitions are
  * supplied below to get you started.
  */
def allCombinablePromotions(allPromotions: Seq[Promotion]): Seq[PromotionCombo] = ???

/**
  * Implement a function to find all PromotionCombos for a given Promotion from
  * given list of Promotions. The function definition is provided.
  */
def combinablePromotions(promotionCode: String, allPromotions: Seq[Promotion]): Seq[PromotionCombo] = ???
