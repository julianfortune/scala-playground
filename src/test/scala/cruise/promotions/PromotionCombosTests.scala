package cruise.promotions

import org.scalatest.flatspec.AnyFlatSpec

class PromotionCombosTests extends AnyFlatSpec:

  "createPromotionGraph" should "create the correct graph" in {
    val promotions = Seq(Promotion("A", Seq("B")), Promotion("B", Seq("A")), Promotion("C", Seq.empty))

    val graph = createPromotionGraph(promotions)

    val expected = Map(
      "A" -> Set("C"),
      "B" -> Set("C"),
      "C" -> Set("A", "B")
    )

    assert(graph == expected)
  }

  "findCombinationsFor" should "find all maximal promotions for a simple graph" in {
    val graph = Map(
      "A" -> Set("B", "C"),
      "B" -> Set("A", "C"),
      "C" -> Set("A", "B"),
      "D" -> Set("E"),
      "E" -> Set("D")
    )
    val frontier = Seq(
      IntermediateCombo(Set("A"), Set("B", "C")),
      IntermediateCombo(Set("B"), Set("A", "C")),
      IntermediateCombo(Set("C"), Set("A", "B")),
      IntermediateCombo(Set("D"), Set("E")),
      IntermediateCombo(Set("E"), Set("D"))
    )

    val combos = findCombinationsFor(graph, frontier, Set.empty, Seq.empty)

    val expected = Set(PromotionCombo("A" :: "B" :: "C" :: Nil), PromotionCombo("D" :: "E" :: Nil))

    // Order is not relevant
    assert(combos.toSet == expected)
  }

  val promotions = Seq(
    Promotion("P1", Seq("P3")), // P1 is not combinable with P3
    Promotion("P2", Seq("P4", "P5")), // P2 is not combinable with P4 and P5
    Promotion("P3", Seq("P1")), // P3 is not combinable with P1
    Promotion("P4", Seq("P2")), // P4 is not combinable with P2
    Promotion("P5", Seq("P2")) // P5 is not combinable with P2
  )

  "allCombinablePromotions" should "return the 'maximal' promotion combinations" in {
    val combinations = allCombinablePromotions(promotions)

    val expected = Seq(
      PromotionCombo(Seq("P2", "P1")),
      PromotionCombo(Seq("P5", "P1", "P4")),
      PromotionCombo(Seq("P3", "P2")),
      PromotionCombo(Seq("P3", "P5", "P4"))
    )

    // Order is not relevant
    assert(combinations.toSet == expected.toSet)
  }

  "combinablePromotions" should "return the correct promotion combinations for 'P1'" in {
    val combinations = combinablePromotions("P1", promotions)

    val expected = Seq(
      PromotionCombo(Seq("P1", "P2")),
      PromotionCombo(Seq("P1", "P5", "P4"))
    )

    // Order is not relevant
    assert(combinations.toSet == expected.toSet)
  }

  it should "return the correct promotion combinations for 'P3'" in {
    val combinations = combinablePromotions("P3", promotions)

    val expected = Seq(
      PromotionCombo(Seq("P3", "P2")),
      PromotionCombo(Seq("P3", "P5", "P4"))
    )

    // Order is not relevant
    assert(combinations.toSet == expected.toSet)
  }

end PromotionCombosTests
