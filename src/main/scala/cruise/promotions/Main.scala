package cruise.promotions

// Input - Promotions:
// Promotion(P1, Seq(P3)) // P1 is not combinable with P3
// Promotion(P2, Seq(P4, P5)) // P2 is not combinable with P4 and P5
// Promotion(P3, Seq(P1)) // P3 is not combinable with P1
// Promotion(P4, Seq(P2)) // P4 is not combinable with P2
// Promotion(P5, Seq(P2)) // P5 is not combinable with P2

// Expected Output for All Promotion Combinations:
// Seq(PromotionCombo(Seq(P1, P2)), PromotionCombo(Seq(P1, P4, P5)), PromotionCombo(Seq(P2, P3)), PromotionCombo(Seq(P3, P4, P5)))

// Expected Output for Promotion Combinations for promotionCode=”P1”:
// Seq(PromotionCombo(Seq(P1, P2)), PromotionCombo(Seq(P1, P4, P5)))

// Expected Output for Promotion Combinations for promotionCode=”P3”:
// Seq(PromotionCombo(Seq(P3, P2)), PromotionCombo(Seq(P3, P4, P5)))

@main def main = println("Hello, world")
