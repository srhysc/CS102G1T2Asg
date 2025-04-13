package entities.comp;
/**
 * The difficulty level of AI Players. 
 * Each level affects the AI's behavior and decision-making complexity.
 */
public enum Difficulty {
    /** AI makes simple or random choices. Best for beginners. */
    EASY,

    /** AI uses a mix of random and strategic moves. Suitable for intermediate players. */
    MEDIUM,

    /** AI applies advanced strategies. Best for experienced players. */
    HARD
}
