package com.savingstracker.app.data

/**
 * Generates the fixed set of individual boxes for the KSh 10,000 challenge.
 * The counts below are chosen so the collective total is EXACTLY 10,000,
 * spread realistically across small/medium/large tiers. This list is the
 * single source of truth for box counts — the sum is verified at runtime
 * via [TOTAL_GOAL] and a startup assertion in the repository.
 */
object BoxGenerator {

    // denomination -> how many individual boxes of that denomination to create
    val COUNTS: Map<Int, Int> = linkedMapOf(
        10 to 20,   // 200
        20 to 15,   // 300
        50 to 10,   // 500
        100 to 10,  // 1000
        200 to 10,  // 2000
        400 to 5,   // 2000
        600 to 5,   // 3000
        1000 to 1   // 1000
    )
    // total = 200+300+500+1000+2000+2000+3000+1000 = 10000

    const val TOTAL_GOAL = 10_000

    fun buildInitialBoxes(): List<SavingsBox> {
        val boxes = mutableListOf<SavingsBox>()
        COUNTS.forEach { (denom, count) ->
            repeat(count) { boxes.add(SavingsBox(denomination = denom)) }
        }
        check(boxes.sumOf { it.denomination } == TOTAL_GOAL) {
            "Box definitions must sum to exactly $TOTAL_GOAL"
        }
        return boxes
    }
}
