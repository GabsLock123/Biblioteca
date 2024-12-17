package biblioteca.model;

public interface RegraCalculoDebito {
    /**
     * Calcula o valor final do débito com base na regra implementada.
     *
     * @param valorBase O valor base do débito.
     * @return O valor final após aplicar a regra.
     */
    int calcularDebito(int valor);
}
