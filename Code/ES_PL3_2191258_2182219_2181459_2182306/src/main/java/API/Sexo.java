package API;

public enum Sexo {
    M,
    F,
    X;

    @Override
    public String toString() {
        return switch (this) {
            case M -> "Masculino";
            case F -> "Femenino";
            case X -> "Outro";
        };
    }
}
