package biblioteca.model;

public class RegraCalculoDebitoAluno implements RegraCalculoDebito {

	@Override
	public int calcularDebito(int valor) {
		return valor;
	}

}
