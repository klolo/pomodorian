package pl.klolo.pomodoro.logic;

@FunctionalInterface
public interface LongBiConsumer {

    /**
     * Performs this operation on the given argument.
     *
     * @param first  the first input argument
     * @param second the second input argument
     */
    void accept(long first, long second);

}
