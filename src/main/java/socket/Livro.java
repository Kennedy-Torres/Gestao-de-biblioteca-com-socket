package socket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Livro {
	private String titulo;
	private String autor;
	private String genero;
	private int numeroDeExemplares;

	public Livro() {}
	
	public Livro(String titulo, String autor, String genero, int numeroDeExemplares) {
		this.titulo = titulo;
		this.autor = autor;
		this.genero = genero;
		this.numeroDeExemplares = numeroDeExemplares;
	}

	/* GETTERS E SETTERS */
	 @JsonProperty("titulo")
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@JsonProperty("autor")
	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}
	
	@JsonProperty("genero")
	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	/**
	 * OBS.: CASO O ARQUIVO NAO TENHA A CHAVE numeroDeExemplares, ELA PASSA A TER.
	 * @return
	 */
	@JsonProperty("numeroDeExemplares")
	public int getNumeroDeExemplares() {
		return numeroDeExemplares;
	}
	public void setNumeroDeExemplares(int numeroDeExemplares) {
		this.numeroDeExemplares = numeroDeExemplares;
	}

	public String toString() {
		return "Titulo: "+ titulo+
				"\nAutor: "+ autor+
				"\nGenero: "+genero+
				"\nNúmero de exemplares: "+numeroDeExemplares;
	}
}
