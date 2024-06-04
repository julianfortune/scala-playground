package cruise.cabin_price

/**
 * A rate is a way to group related prices together. A rate is defined by its Rate Code and which Rate Group
 * it belongs to. For example. (MilAB, Military) and (Sen123, Senior)
 */
case class Rate(rateCode: String, rateGroup: String)

/**
 * ​The price for a specific cabin on a specific cruise. All cabin prices will have a single rate attached.
 */
case class CabinPrice(cabinCode: String, rateCode: String, price: BigDecimal)

case class BestGroupPrice(
  cabinCode: String,
  rateCode: String,
  price: BigDecimal,
  rateGroup: String
)

def getBestGroupPricesForCabinAndGroup(
  prices: Seq[CabinPrice],
  rateGroupForCode: Map[String, String]
): Map[(String, String), BestGroupPrice] = {
  prices.foldLeft(Map.empty) { (bestPrices, current) =>
    val maybeRateGroup = rateGroupForCode.get(current.rateCode)

    // NOTE: Silently ignores `CabinPrice`s with unrecognized `rateCode`s
    val updatedBestPrices = maybeRateGroup.flatMap { rateGroup =>
      val currentIsBestPrice = bestPrices.get((current.cabinCode, rateGroup)) match
        case Some(existingGroupPrice) => current.price < existingGroupPrice.price
        case None                     => true

      if (currentIsBestPrice) {
        val newBestGroupPrice = BestGroupPrice(current.cabinCode, current.rateCode, current.price, rateGroup)
        Some(bestPrices + ((current.cabinCode, rateGroup) -> newBestGroupPrice))
      } else None
    }

    updatedBestPrices.getOrElse(bestPrices)
  }
}

/**
 * Takes a list of rates and a list of prices and returns the best price for each rate group.
 */
def getBestGroupPrices(
  rates: Seq[Rate],
  prices: Seq[CabinPrice]
): Seq[BestGroupPrice] = {
  val rateGroupForCode = rates.map(r => r.rateCode -> r.rateGroup).toMap

  return getBestGroupPricesForCabinAndGroup(prices, rateGroupForCode).values.toSeq
}
