package dev.rico.core.functional;

import java.util.Optional;

public abstract class Either<L, R> {

    public static <L, R> Either<L, R> left(final L left) {
        return new Left<>(left);
    }

    public static <L, R> Either<L, R> right(final R right) {
        return new Right<>(right);
    }

    public abstract boolean isRight();

    public abstract boolean isLeft();

    public abstract Optional<L> left();

    public abstract Optional<R> right();

    public abstract L getLeft();

    public abstract R getRight();

    private static class Left<L, R> extends Either<L, R> {

        private final L value;

        public Left(L value) {
            this.value = value;
        }

        @Override
        public final boolean isRight() {
            return false;
        }

        public final boolean isLeft() {
            return true;
        }

        public final Optional<L> left() {
            return Optional.ofNullable(value);
        }

        public final Optional<R> right() {
            return Optional.empty();
        }

        public final L getLeft() {
            return value;
        }

        public final R getRight() {
            throw new IllegalStateException("Right is not defined");
        }
    }

    private static class Right<L, R> extends Either<L, R> {

        private final R value;

        public Right(R value) {
            this.value = value;
        }

        @Override
        public final boolean isRight() {
            return true;
        }

        public final boolean isLeft() {
            return false;
        }

        public final Optional<L> left() {
            return Optional.empty();
        }

        public final Optional<R> right() {
            return Optional.ofNullable(value);
        }

        public final L getLeft() {
            throw new IllegalStateException("Left is not defined");
        }

        public final R getRight() {
            return value;
        }
    }
}
