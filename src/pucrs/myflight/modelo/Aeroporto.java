package pucrs.myflight.modelo;

public class Aeroporto implements Comparable<Aeroporto> {
	private String codigo;
	private String nome;
	private Geo loc;
	private String pais;
	
	public Aeroporto(String codigo, String nome, Geo loc, String pais) {
		this.codigo = codigo;
		this.nome = nome;
		this.loc = loc;
		this.pais = pais;
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public String getNome() {
		return nome;
	}
	
	public Geo getLocal() {
		return loc;
	}

	public String getPais() {
		return pais;
	}

    @Override
    public String toString() {
        return codigo + " - " + nome + " [" + loc + "] - " + pais;
    }

	@Override
	public int compareTo(Aeroporto outro) {
		return this.nome.compareTo(outro.nome);
	}
}
