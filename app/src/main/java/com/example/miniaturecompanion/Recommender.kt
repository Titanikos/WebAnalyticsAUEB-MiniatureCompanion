package com.example.miniaturecompanion

const val SHIPPING = 6.0 // approximate overseas shipping, sample value

/**
 * Content-based recommender (mockup). Each item gets a match score from three inputs:
 *   match = 0.5 * compatibility + 0.3 * fit with the planned paint scheme + 0.2 * popularity
 * Items are then filtered by region (unless overseas shops are allowed) and ranked by score.
 * In a real app the inputs would be learned from compatibility data and user behaviour.
 */
object Recommender {

    fun match(p: Product): Int = (0.5 * p.compat + 0.3 * p.scheme + 0.2 * p.popularity).toInt()

    fun label(score: Int): String = when {
        score >= 85 -> "Great fit"
        score >= 70 -> "Good fit"
        else -> "Possible fit"
    }

    fun isLocal(brand: Brand, region: Region) = region in brand.regions

    fun visible(brand: Brand, region: Region, overseas: Boolean) = overseas || isLocal(brand, region)

    fun rank(items: List<Product>, region: Region, overseas: Boolean): List<Product> =
        items.filter { visible(it.brand, region, overseas) }.sortedByDescending { match(it) }

    /** The sculpting set matters more when the user is considering conversion parts. */
    fun toolsFor(model: Model): List<Product> = TOOLS.map {
        if (it.brand == Brand.GS) {
            if (model.parts.isNotEmpty())
                it.copy(compat = 90, reason = "Useful for fitting the conversion parts you are considering.")
            else
                it.copy(compat = 60, reason = "Handy for filling gaps, but you may not need it for this kit.")
        } else it
    }
}