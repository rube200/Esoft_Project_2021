package API;

public enum Sexo {
    M,
    F;

    @Override
    public String toString() {
        return switch (this) {
            case M -> "Masculino";
            case F -> "Femenino";
        };
    }
}
