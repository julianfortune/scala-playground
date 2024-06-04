package cruise.cabin_price

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.flatspec.AnyFlatSpec

class BestGroupPriceTests extends AnyFlatSpec:

  "getBestGroupPrices" should "identify the best price for each group" in {
    val inputRates = Seq(
      Rate("M1", "Military"),
      Rate("M2", "Military"),
      Rate("S1", "Senior"),
      Rate("S2", "Senior")
    )

    val inputCabins = Seq(
      CabinPrice("CA", "M1", 200.00),
      CabinPrice("CA", "M2", 250.00),
      CabinPrice("CA", "S1", 225.00),
      CabinPrice("CA", "S2", 260.00),
      CabinPrice("CB", "M1", 230.00),
      CabinPrice("CB", "M2", 260.00),
      CabinPrice("CB", "S1", 245.00),
      CabinPrice("CB", "S2", 270.00)
    )

    val expectedOutput = Seq(
      BestGroupPrice("CA", "M1", 200.00, "Military"),
      BestGroupPrice("CA", "S1", 225.00, "Senior"),
      BestGroupPrice("CB", "M1", 230.00, "Military"),
      BestGroupPrice("CB", "S1", 245.00, "Senior")
    )

    assert(getBestGroupPrices(inputRates, inputCabins) == expectedOutput)
  }

  it should "handle unrecognized rate codes gracefully" in {
    val rates = Seq(
      Rate("M1", "Military")
    )

    val cabins = Seq(
      CabinPrice("CA", "M1", 200.00),
      CabinPrice("CA", "M2", 250.00),
      CabinPrice("CA", "S1", 225.00)
    )

    val expectedOutput = Seq(
      BestGroupPrice("CA", "M1", 200.00, "Military")
    )

    assert(getBestGroupPrices(rates, cabins) == expectedOutput)
  }

end BestGroupPriceTests
