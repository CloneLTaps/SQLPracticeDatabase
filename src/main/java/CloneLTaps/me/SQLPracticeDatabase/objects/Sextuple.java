package CloneLTaps.me.SQLPracticeDatabase.objects;

import java.util.Objects;

public class Sextuple<A, B, C, D, E, F> {
    private final A first;
    private final B second;
    private final C third;
    private final D fourth;
    private final E fifth;
    private final F sixth;

    public Sextuple(A first, B second, C third, D fourth, E fifth, F sixth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
        this.sixth = sixth;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Sextuple)) return false;

        final Sextuple<?, ?, ?, ?, ?, ?> b = (Sextuple<?, ?, ?, ?, ?, ?>) o;
        return Objects.equals(this.first, b.first) && Objects.equals(this.second, b.second) && Objects.equals(this.third, b.third) &&
                Objects.equals(this.fourth, b.fourth) && Objects.equals(this.fifth, b.fifth) && Objects.equals(this.sixth, b.sixth);
    }

    @Override
    public String toString() {
        return first.toString() + second.toString() + third.toString() + fourth.toString() + fifth.toString() + sixth.toString();
    }
}
