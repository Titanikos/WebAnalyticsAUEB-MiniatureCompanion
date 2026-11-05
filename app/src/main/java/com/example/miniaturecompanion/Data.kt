package com.example.miniaturecompanion

// Sample data only. Names, prices and availability are illustrative.

enum class Region(val label: String) { GR("Greece"), UK("United Kingdom"), US("United States") }

enum class Brand(val label: String, val regions: Set<Region>) {
    GW("Games Workshop", Region.values().toSet()),
    WL("Warlord Games", Region.values().toSet()),
    VA("Vallejo", Region.values().toSet()),
    AP("Army Painter", Region.values().toSet()),
    AK("AK Interactive", setOf(Region.GR, Region.UK)),
    PA("Pro Acryl", setOf(Region.US)),
    GS("Green Stuff World", Region.values().toSet()),
    KR("Kromlech", Region.values().toSet()),
    AF("Archie's Forge", setOf(Region.UK))
}

/** A cheaper alternative paint from another brand. */
data class Alt(val brand: Brand, val name: String, val price: Double)

/**
 * compat, scheme and popularity are 0..100 inputs that the recommender combines into a match score.
 * In a real app they would come from tagged compatibility data, the user's chosen paint scheme,
 * and how often other users pick the item for the same model.
 */
data class Product(
    val brand: Brand,
    val name: String,
    val price: Double,
    val compat: Int,
    val scheme: Int,
    val popularity: Int,
    val reason: String,
    val warning: String? = null,
    val alt: Alt? = null
)

data class Tutorial(val title: String, val query: String, val length: String)

data class Model(
    val id: String,
    val brand: Brand,
    val name: String,
    val game: String,
    val spec: String,
    val scheme: String,
    val parts: List<Product>,
    val paints: List<Product>,
    val tutorials: List<Tutorial>
)

val TOOLS = listOf(
    Product(Brand.GW, "Fine Detail Cutters", 16.0, 95, 85, 85, "Cut parts from the sprue cleanly."),
    Product(Brand.AP, "Precision Hobby Knife", 7.0, 90, 80, 75, "Removes mould lines from plastic."),
    Product(Brand.GS, "Sculpting Tool Set", 12.0, 0, 70, 65, "")
)

val BRUSHES = listOf(
    Product(Brand.GW, "Layer Brush (small)", 6.0, 95, 90, 85, "Edge highlights and fine detail."),
    Product(Brand.AP, "Hobby Brush, Detail", 5.0, 90, 80, 80, "Good value for base coats and detail."),
    Product(Brand.GS, "Sable Brush Set", 18.0, 80, 75, 70, "Higher-end set for careful highlighting.")
)

private fun paint(role: String, gw: String, price: Double, c: Int, s: Int, p: Int, why: String, alt: Alt) =
    Product(Brand.GW, "$role: $gw", price, c, s, p, why, alt = alt)

val MODELS = listOf(
    Model(
        "int", Brand.GW, "Space Marines Intercessors", "Warhammer 40,000",
        "32 mm infantry, plastic kit", "blue armour with gold trim",
        parts = listOf(
            Product(Brand.KR, "Ornate shoulder pad set (10)", 14.0, 100, 80, 90, "Popular upgrade for this kit and sized for 32 mm marines."),
            Product(Brand.AF, "Alternative head set (5)", 12.0, 90, 75, 85, "Often bought together with custom shoulder pads."),
            Product(Brand.KR, "Grenade and pouch bits", 9.0, 90, 50, 55, "Fits the kit, but suits veteran-style builds more than a plain battle line.")
        ),
        paints = listOf(
            paint("Armour base coat", "Macragge Blue", 4.2, 100, 90, 90, "The usual base for a blue armour scheme.", Alt(Brand.VA, "Ultramarine (Model Color)", 2.6)),
            paint("Shade", "Nuln Oil", 4.5, 95, 85, 85, "Adds depth in the recesses of the armour.", Alt(Brand.AP, "Dark Tone wash", 3.2)),
            paint("Weapons and vents", "Leadbelcher", 4.2, 90, 80, 85, "Metal for bolters and exhaust vents.", Alt(Brand.AK, "Steel (3rd Gen)", 2.9)),
            paint("Gold trim", "Retributor Armour", 4.2, 85, 75, 80, "Matches the gold trim in your scheme.", Alt(Brand.PA, "Bright Gold", 3.1))
        ),
        tutorials = listOf(
            Tutorial("Painting blue Intercessors step by step", "paint ultramarines intercessors tutorial", "12 min"),
            Tutorial("Edge highlighting power armour", "edge highlighting space marine tutorial", "9 min"),
            Tutorial("Fitting conversion shoulder pads", "space marine shoulder pad conversion tutorial", "7 min")
        )
    ),
    Model(
        "nec", Brand.GW, "Necron Warriors", "Warhammer 40,000",
        "32 mm infantry, plastic kit", "metallic bodies with green glow",
        parts = listOf(
            Product(Brand.AF, "Cabling and glyph accessories", 11.0, 70, 60, 65, "Few third-party parts exist for this kit, so confidence is lower.", "Check the part fits the 32 mm base before buying.")
        ),
        paints = listOf(
            paint("Metal body", "Leadbelcher", 4.2, 100, 90, 80, "Base metal for the whole model.", Alt(Brand.AK, "Steel (3rd Gen)", 2.9)),
            paint("Wash", "Nuln Oil", 4.5, 95, 85, 75, "Darkens joints and panel lines.", Alt(Brand.AP, "Dark Tone wash", 3.2)),
            paint("Glow effect", "Warpstone Glow", 4.2, 80, 75, 85, "Gives the green glow on weapons and eyes.", Alt(Brand.VA, "Fluorescent Green", 2.8))
        ),
        tutorials = listOf(
            Tutorial("Painting Necron Warriors quickly", "paint necron warriors tutorial", "10 min"),
            Tutorial("Glowing green weapons", "necron green glow tutorial", "8 min")
        )
    ),
    Model(
        "par", Brand.WL, "British Paratroopers", "Bolt Action",
        "28 mm infantry, plastic and metal", "olive camouflage smocks",
        parts = listOf(
            Product(Brand.KR, "28 mm head swap set (6)", 11.0, 60, 55, 60, "Possible match only. Few conversion sets target this range.", "Scale check: confirm head size against the kit before buying.")
        ),
        paints = listOf(
            paint("Smock base", "Castellan Green", 4.2, 95, 85, 80, "A good base for olive field clothing.", Alt(Brand.AP, "Army Green", 2.7)),
            paint("Skin", "Kislev Flesh", 4.2, 90, 80, 75, "Natural skin tone for hands and faces.", Alt(Brand.VA, "Basic Skin Tone", 2.6)),
            paint("Shade", "Agrax Earthshade", 4.5, 90, 75, 75, "Warm shade that suits cloth and webbing.", Alt(Brand.AP, "Strong Tone wash", 3.2))
        ),
        tutorials = listOf(
            Tutorial("Painting WW2 paratroopers", "paint bolt action paratroopers tutorial", "14 min"),
            Tutorial("Camouflage smocks made simple", "paint camouflage smock miniature tutorial", "11 min")
        )
    ),
    Model(
        "ger", Brand.WL, "Late War German Infantry", "Bolt Action",
        "28 mm infantry, plastic kit", "field grey uniforms",
        parts = emptyList(),
        paints = listOf(
            paint("Uniform base", "Mechanicus Standard Grey", 4.2, 95, 85, 75, "A common choice for field grey uniforms.", Alt(Brand.PA, "Slate Grey", 3.0)),
            paint("Skin", "Kislev Flesh", 4.2, 90, 80, 75, "Natural skin tone for hands and faces.", Alt(Brand.VA, "Basic Skin Tone", 2.6)),
            paint("Shade", "Agrax Earthshade", 4.5, 85, 75, 75, "Warm shade that suits cloth and leather.", Alt(Brand.AP, "Strong Tone wash", 3.2))
        ),
        tutorials = listOf(
            Tutorial("Painting German infantry fast", "paint bolt action german infantry tutorial", "13 min"),
            Tutorial("Field grey uniforms", "paint field grey miniature tutorial", "9 min")
        )
    )
)