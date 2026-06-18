"""
Geração de figuras dos testes de carga do GoTicket (TCC).

Lê valores AGREGADOS já extraídos de k6/results/*.summary.json e *.verify.md
(cada número abaixo cita o arquivo de origem) e produz figuras vetoriais
em PDF + PNG (300 dpi) em k6/results/figuras/.

Uso:
    pip install matplotlib
    python gerar_graficos.py

Observação: gráficos de SÉRIE TEMPORAL (latência/RPS ao longo da rampa) não
saem daqui — os summaries só guardam agregados. Para eles, re-rodar o k6 com
`--out csv` e plotar a série separadamente.
"""
import os
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.ticker import FuncFormatter

# ----------------------------------------------------------------------------
# Dados (curados dos resultados — fonte citada em cada bloco)
# ----------------------------------------------------------------------------
DADOS = {
    # Fonte: results/A-overselling-protected.summary.json / .verify.md
    "A": dict(vus=3000, reqs=32873, c201=100, c409=32773, c5xx=0, p95_ms=15311,
              cota=100, vendidos=100, overselling=0),
    # Fonte: results/B-payment-protected.summary.json / .verify.md
    "B": dict(vus=700, reqs=52187, c201=28866, c409=23321, c5xx=0, p95_ms=1754,
              pedidos=111, duplicatas=0),
    # Fonte: results/C-availability-noqueue.summary.json / .verify.md
    "C": dict(arrival_pico=1200, reqs=18243, rps_efetivo=53.6, dropped=184256,
              c201=1789, c409=4824, timeout=11630, p95_ms=60001,
              criados=6000, confirmados=1789, ambiguos=4211, overselling=0,
              sucesso_pct=9.8),
    # Fonte: results/E-journey-protected.summary.json / .verify.md
    "E": dict(arrival_pico=30, reqs=48689, checkout_p95_ms=212, sucesso_pct=99.68,
              fail_order=3, fail_confirm=8, completados=3463, queue_wait_p95_ms=135073,
              etapas={"Entrar na fila": 15, "Escolher data/setor": 23,
                      "Reserva (pedido)": 131, "Confirmar pagamento": 103,
                      "Buscar ticket": 18}),
}

# ----------------------------------------------------------------------------
# Estilo
# ----------------------------------------------------------------------------
plt.rcParams.update({
    "font.family": "DejaVu Sans",
    "font.size": 11,
    "axes.titlesize": 12.5,
    "axes.titleweight": "bold",
    "axes.labelsize": 11,
    "axes.spines.top": False,
    "axes.spines.right": False,
    "axes.grid": True,
    "axes.axisbelow": True,
    "grid.alpha": 0.25,
    "grid.linewidth": 0.6,
})

COR_C    = "#D85A30"  # sem fila / problema (coral)
COR_E    = "#1D9E75"  # com fila / saudável (teal)
COR_NEU  = "#888780"  # neutro (gray)
COR_WARN = "#BA7517"  # destaque (amber)

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "results", "figuras")
os.makedirs(OUT, exist_ok=True)


def br(n, dec=0):
    """Formata número no padrão pt-BR (milhar com '.', decimal com ',')."""
    s = f"{n:,.{dec}f}"
    return s.replace(",", "X").replace(".", ",").replace("X", ".")


def salvar(fig, nome):
    for ext in ("pdf", "png"):
        fig.savefig(os.path.join(OUT, f"{nome}.{ext}"), bbox_inches="tight", dpi=300)
    plt.close(fig)
    print(f"  ok  {nome}.pdf / .png")


# ----------------------------------------------------------------------------
# Figuras
# ----------------------------------------------------------------------------
def fig1_sucesso():
    c, e = DADOS["C"]["sucesso_pct"], DADOS["E"]["sucesso_pct"]
    fig, ax = plt.subplots(figsize=(6, 3.3))
    bars = ax.bar(["C — sem fila", "E — com fila"], [c, e], color=[COR_C, COR_E], width=0.55)
    ax.set_ylim(0, 108)
    ax.set_ylabel("Taxa de sucesso (%)")
    ax.set_title("Sucesso entre requisições concluídas")
    ax.yaxis.set_major_formatter(FuncFormatter(lambda x, _: f"{int(x)}%"))
    for b, v in zip(bars, [c, e]):
        ax.text(b.get_x() + b.get_width() / 2, v + 1.5, br(v, 2) + "%",
                ha="center", va="bottom", fontweight="bold")
    salvar(fig, "fig1_sucesso_c_vs_e")


def fig2_latencia():
    c, e = DADOS["C"]["p95_ms"], DADOS["E"]["checkout_p95_ms"]
    fig, ax = plt.subplots(figsize=(6, 3.3))
    bars = ax.bar(["C — sem fila", "E — com fila"], [c, e], color=[COR_C, COR_E], width=0.55)
    ax.set_yscale("log")
    ax.set_ylim(50, 200000)
    ax.set_ylabel("Latência p95 do checkout (ms, escala log)")
    ax.set_title("Latência do checkout (p95) — 283× mais rápido com fila")
    for b, v in zip(bars, [c, e]):
        ax.text(b.get_x() + b.get_width() / 2, v * 1.18, br(v) + " ms",
                ha="center", va="bottom", fontweight="bold")
    salvar(fig, "fig2_latencia_c_vs_e")


def fig3_ambiguos():
    d = DADOS["C"]
    conf, amb = d["confirmados"], d["ambiguos"]
    fig, ax = plt.subplots(figsize=(6.6, 2.5))
    ax.barh([0], [conf], color=COR_E, label="Confirmados ao cliente")
    ax.barh([0], [amb], left=[conf], color=COR_C, label="Ambíguos (criados sem confirmação)")
    ax.set_xlim(0, 6000)
    ax.set_yticks([])
    ax.set_xlabel("Pedidos")
    ax.set_title("Cenário C: dos 6.000 pedidos criados, só 1.789 confirmados")
    ax.text(conf / 2, 0, br(conf), ha="center", va="center", color="white", fontweight="bold")
    ax.text(conf + amb / 2, 0, br(amb), ha="center", va="center", color="white", fontweight="bold")
    ax.legend(loc="upper center", bbox_to_anchor=(0.5, -0.35), ncol=2, frameon=False, fontsize=10)
    salvar(fig, "fig3_pedidos_ambiguos_c")


def fig4_saturacao():
    d = DADOS["C"]
    vals = [d["arrival_pico"], d["rps_efetivo"]]
    fig, ax = plt.subplots(figsize=(6, 3.3))
    bars = ax.bar(["Vazão oferecida\n(pico)", "Vazão efetiva"], vals,
                  color=[COR_NEU, COR_C], width=0.55)
    ax.set_yscale("log")
    ax.set_ylim(10, 4000)
    ax.set_ylabel("Requisições/s (escala log)")
    ax.set_title("Cenário C: o checkout sem fila satura muito abaixo da demanda")
    for b, v in zip(bars, vals):
        rotulo = (br(v) if v >= 100 else br(v, 1)) + " req/s"
        ax.text(b.get_x() + b.get_width() / 2, v * 1.18, rotulo,
                ha="center", va="bottom", fontweight="bold")
    ax.text(0.5, 0.93, f"{br(d['dropped'])} chegadas descartadas (dropped)",
            transform=ax.transAxes, ha="center", color=COR_WARN, fontsize=10)
    salvar(fig, "fig4_saturacao_c")


def fig5_etapas_e():
    etapas = DADOS["E"]["etapas"]
    nomes = list(etapas.keys())
    vals = list(etapas.values())
    fig, ax = plt.subplots(figsize=(6.6, 3.4))
    bars = ax.barh(nomes, vals, color=COR_E)
    ax.invert_yaxis()
    ax.set_xlim(0, 165)
    ax.set_xlabel("Latência p95 (ms)")
    ax.set_title("Cenário E: latência por etapa (zona protegida < 250 ms)")
    for b, v in zip(bars, vals):
        ax.text(v + 3, b.get_y() + b.get_height() / 2, br(v) + " ms",
                va="center", fontweight="bold")
    salvar(fig, "fig5_latencia_etapas_e")


def fig6_status_abc():
    testes = ["A\n(3.000 VUs)", "B\n(700 VUs)", "C\n(1.200 req/s)"]
    s201 = [DADOS["A"]["c201"], DADOS["B"]["c201"], DADOS["C"]["c201"]]
    s409 = [DADOS["A"]["c409"], DADOS["B"]["c409"], DADOS["C"]["c409"]]
    sto  = [0, 0, DADOS["C"]["timeout"]]
    base = [a + b for a, b in zip(s201, s409)]
    fig, ax = plt.subplots(figsize=(6.6, 3.6))
    ax.bar(testes, s201, color=COR_E, label="201 (sucesso/replay)")
    ax.bar(testes, s409, bottom=s201, color=COR_NEU, label="409 (esgotado/idempotência)")
    ax.bar(testes, sto, bottom=base, color=COR_C, label="timeout (status 0)")
    ax.set_ylabel("Requisições")
    ax.set_title("Distribuição de respostas por teste (A, B, C)")
    ax.legend(loc="upper center", bbox_to_anchor=(0.5, -0.16), ncol=3, frameon=False, fontsize=9.5)
    salvar(fig, "fig6_status_abc")


def fig7_integridade_a():
    d = DADOS["A"]
    labels = ["Cota", "Vendidos", "Overselling"]
    vals = [d["cota"], d["vendidos"], d["overselling"]]
    fig, ax = plt.subplots(figsize=(5.6, 3.3))
    bars = ax.bar(labels, vals, color=[COR_NEU, COR_E, COR_C], width=0.55)
    ax.set_ylim(0, 122)
    ax.set_ylabel("Ingressos")
    ax.set_title("Cenário A: 3.000 VUs disputando 100 ingressos")
    for b, v in zip(bars, vals):
        ax.text(b.get_x() + b.get_width() / 2, v + 2, br(v),
                ha="center", va="bottom", fontweight="bold")
    salvar(fig, "fig7_integridade_a")


if __name__ == "__main__":
    print(f"Gerando figuras em: {OUT}")
    fig1_sucesso()
    fig2_latencia()
    fig3_ambiguos()
    fig4_saturacao()
    fig5_etapas_e()
    fig6_status_abc()
    fig7_integridade_a()
    print("Concluído.")
