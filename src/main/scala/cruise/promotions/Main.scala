package cruise.promotions

val promotions = Seq(
  Promotion("P1", Seq("P3")), // P1 is not combinable with P3
  Promotion("P2", Seq("P4", "P5")), // P2 is not combinable with P4 and P5
  Promotion("P3", Seq("P1")), // P3 is not combinable with P1
  Promotion("P4", Seq("P2")), // P4 is not combinable with P2
  Promotion("P5", Seq("P2")) // P5 is not combinable with P2
)

@main def main = {
  println("----------- Part 1 -----------")
  println(allCombinablePromotions(promotions))

  println("----------- Part 2 -----------")
  println(s"P1 -> ${combinablePromotions("P1", promotions)}")
  println(s"P3 -> ${combinablePromotions("P3", promotions)}")
}
