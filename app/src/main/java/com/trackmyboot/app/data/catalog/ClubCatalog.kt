package com.trackmyboot.app.data.catalog

import com.trackmyboot.app.data.model.Club

/**
 * Clubes e seleções disponíveis para o usuário seguir.
 * Os "aliases" ajudam a reconhecer o clube nos títulos dos produtos das lojas.
 */
object ClubCatalog {
    val clubs: List<Club> = listOf(
        // Brasil
        Club("flamengo", "Flamengo", listOf("Mengão", "CRF"), "Brasil", 0xFFC8102E, 0xFF000000),
        Club("palmeiras", "Palmeiras", listOf("Verdão", "SEP"), "Brasil", 0xFF006437, 0xFFFFFFFF),
        Club("corinthians", "Corinthians", listOf("Timão", "SCCP"), "Brasil", 0xFF000000, 0xFFFFFFFF),
        Club("sao-paulo", "São Paulo", listOf("SPFC", "Tricolor Paulista"), "Brasil", 0xFFE4002B, 0xFF000000),
        Club("santos", "Santos", listOf("Peixe", "Santos FC"), "Brasil", 0xFFFFFFFF, 0xFF000000),
        Club("vasco", "Vasco", listOf("Vasco da Gama", "CRVG"), "Brasil", 0xFF000000, 0xFFFFFFFF),
        Club("botafogo", "Botafogo", listOf("Fogão"), "Brasil", 0xFF000000, 0xFFFFFFFF),
        Club("fluminense", "Fluminense", listOf("Flu", "Tricolor Carioca"), "Brasil", 0xFF7A1E3C, 0xFF00663E),
        Club("gremio", "Grêmio", listOf("Tricolor Gaúcho"), "Brasil", 0xFF0D80BF, 0xFF000000),
        Club("internacional", "Internacional", listOf("Inter", "Colorado"), "Brasil", 0xFFE30613, 0xFFFFFFFF),
        Club("atletico-mg", "Atlético Mineiro", listOf("Atlético-MG", "Atletico MG", "Galo", "CAM"), "Brasil", 0xFF000000, 0xFFFFFFFF),
        Club("cruzeiro", "Cruzeiro", listOf("Raposa"), "Brasil", 0xFF003DA5, 0xFFFFFFFF),
        Club("bahia", "Bahia", listOf("EC Bahia", "Esquadrão"), "Brasil", 0xFF0057A8, 0xFFE30613),
        Club("vitoria", "Vitória", listOf("EC Vitória", "Leão da Barra"), "Brasil", 0xFFE30613, 0xFF000000),
        Club("fortaleza", "Fortaleza", listOf("Leão do Pici"), "Brasil", 0xFF0047AB, 0xFFE30613),
        Club("ceara", "Ceará", listOf("Vozão"), "Brasil", 0xFF000000, 0xFFFFFFFF),
        Club("sport", "Sport", listOf("Sport Recife", "Leão da Ilha"), "Brasil", 0xFFE30613, 0xFF000000),
        Club("athletico-pr", "Athletico Paranaense", listOf("Athletico-PR", "Athletico", "Furacão", "CAP"), "Brasil", 0xFFE30613, 0xFF000000),
        Club("coritiba", "Coritiba", listOf("Coxa"), "Brasil", 0xFF00543C, 0xFFFFFFFF),
        Club("bragantino", "Red Bull Bragantino", listOf("Bragantino", "Massa Bruta"), "Brasil", 0xFFFFFFFF, 0xFFE30613),
        Club("juventude", "Juventude", listOf("Ju"), "Brasil", 0xFF00843D, 0xFFFFFFFF),
        Club("goias", "Goiás", listOf("Verdão do Cerrado"), "Brasil", 0xFF006437, 0xFFFFFFFF),
        Club("mirassol", "Mirassol", listOf("Leão de Mirassol"), "Brasil", 0xFFFFD100, 0xFF006437),
        Club("remo", "Remo", listOf("Clube do Remo", "Leão Azul"), "Brasil", 0xFF00205B, 0xFFFFFFFF),
        Club("paysandu", "Paysandu", listOf("Papão"), "Brasil", 0xFF66B2E4, 0xFFFFFFFF),
        // Seleções
        Club("brasil", "Seleção Brasileira", listOf("Brasil", "CBF", "Brazil"), "Seleções", 0xFFFFDF00, 0xFF009C3B),
        Club("argentina", "Argentina", listOf("AFA"), "Seleções", 0xFF75AADB, 0xFFFFFFFF),
        Club("portugal", "Portugal", listOf("FPF"), "Seleções", 0xFFDA291C, 0xFF046A38),
        Club("franca", "França", listOf("France"), "Seleções", 0xFF002395, 0xFFED2939),
        // Exterior
        Club("real-madrid", "Real Madrid", listOf("Madrid"), "Exterior", 0xFFFFFFFF, 0xFFFEBE10),
        Club("barcelona", "Barcelona", listOf("Barça", "FC Barcelona"), "Exterior", 0xFFA50044, 0xFF004D98),
        Club("manchester-city", "Manchester City", listOf("Man City", "City"), "Exterior", 0xFF6CABDD, 0xFFFFFFFF),
        Club("manchester-united", "Manchester United", listOf("Man United", "Man Utd"), "Exterior", 0xFFDA291C, 0xFF000000),
        Club("liverpool", "Liverpool", listOf("LFC"), "Exterior", 0xFFC8102E, 0xFFFFFFFF),
        Club("arsenal", "Arsenal", listOf("Gunners"), "Exterior", 0xFFEF0107, 0xFFFFFFFF),
        Club("chelsea", "Chelsea", listOf("Blues"), "Exterior", 0xFF034694, 0xFFFFFFFF),
        Club("psg", "Paris Saint-Germain", listOf("PSG", "Paris"), "Exterior", 0xFF004170, 0xFFDA291C),
        Club("bayern", "Bayern de Munique", listOf("Bayern", "Bayern Munich"), "Exterior", 0xFFDC052D, 0xFFFFFFFF),
        Club("juventus", "Juventus", listOf("Juve"), "Exterior", 0xFF000000, 0xFFFFFFFF),
        Club("milan", "Milan", listOf("AC Milan"), "Exterior", 0xFFFB090B, 0xFF000000),
        Club("inter-milao", "Inter de Milão", listOf("Inter Milan", "Internazionale"), "Exterior", 0xFF0068A8, 0xFF000000),
        Club("boca", "Boca Juniors", listOf("Boca"), "Exterior", 0xFF00338D, 0xFFFFD100),
        Club("river", "River Plate", listOf("River"), "Exterior", 0xFFFFFFFF, 0xFFE30613),
        Club("benfica", "Benfica", listOf("SL Benfica"), "Exterior", 0xFFE30613, 0xFFFFFFFF),
    )

    private val byId = clubs.associateBy { it.id }

    fun find(id: String): Club? = byId[id]
}
