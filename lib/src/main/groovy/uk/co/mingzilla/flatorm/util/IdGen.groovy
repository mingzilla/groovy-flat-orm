package uk.co.mingzilla.flatorm.util

class IdGen {
    private static final int LIMIT = 10000
    private static final int MIN_VALUE = -99999999
    private static final int MAX_VALUE = -98999999
    private Set<Integer> generatedNumbers = new HashSet<>()
    private Random random = new Random()

    static IdGen create() {
        return new IdGen()
    }

    Integer getInt() {
        if (generatedNumbers.size() >= LIMIT) {
            throw new IllegalStateException("All unique numbers have been generated")
        }

        int newNumber
        do {
            newNumber = random.nextInt((MAX_VALUE - MIN_VALUE) + 1) + MIN_VALUE
        } while (generatedNumbers.contains(newNumber))

        generatedNumbers.add(newNumber)
        return newNumber
    }

    void clear() {
        generatedNumbers.clear()
    }

    static boolean isGeneratedId(def num) {
        Integer id = InFn.asInteger(num)
        if (!id) return false
        return id <= MAX_VALUE && id >= MIN_VALUE
    }
}