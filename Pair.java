public class Pair<T, U> {
    public T first;
    public U second;

    public Pair() {
        first = null;
        second = null;
    }

    public Pair(T a, U b) {
        first = a;
        second = b;
    }

    public boolean equals(Pair other) {
        return first.equals(other.first) && second.equals(other.second)
                || first.equals(other.second) && second.equals(other.first);
    }

    public boolean isEmpty() {
        return first == null && second == null;
    }
}