package com.example.data

object CatalogData {
    val sampleCast = listOf(
        CastMember("Feyyaz Yiğit", "Yılmaz", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop"),
        CastMember("Kıvanç Tatlıtuğ", "Kadri", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop"),
        CastMember("Gökhan Yıkılkan", "İlkkan", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300&auto=format&fit=crop"),
        CastMember("Haluk Bilginer", "Agâh Beyoğlu", "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=300&auto=format&fit=crop")
    )

    val initialCatalog: List<MediaItem> = listOf(
        MediaItem(
            id = "interstellar-movie",
            title = "Interstellar",
            originalTitle = "Interstellar",
            type = "MOVIE",
            year = 2014,
            runtime = "2sa 49dk",
            rating = 8.7f,
            posterUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop",
            backdropUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=1200&auto=format&fit=crop",
            overview = "Dünyanın kuraklık ve kıtlıkla yüzleştiği gelecekte, insanlığın hayatta kalabilmesi için solucan deliğinden geçerek yeni yaşanabilir gezegenler arayan astronot grubunun epik yolculuğu.",
            genres = listOf("Bilim Kurgu", "Macera", "Dram"),
            trailerUrl = "https://www.youtube.com/watch?v=zSWdZVtXT7E",
            cast = listOf(
                CastMember("Matthew McConaughey", "Cooper", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop"),
                CastMember("Anne Hathaway", "Brand", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop"),
                CastMember("Jessica Chastain", "Murph", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&auto=format&fit=crop")
            ),
            watchStatus = "WATCHED",
            userRating = 10,
            userNotes = "Sinema tarihinin en iyi görsel ve müzikal şaheserlerinden biri. Zimmer müziği tüyleri diken diken ediyor.",
            watchedEpisodes = 0,
            totalEpisodes = 0
        ),
        MediaItem(
            id = "breaking-bad-tv",
            title = "Breaking Bad",
            originalTitle = "Breaking Bad",
            type = "TV",
            year = 2008,
            runtime = "5 Sezon (62 Bölüm)",
            rating = 9.5f,
            posterUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600&auto=format&fit=crop",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop",
            overview = "Kanser olduğunu öğrenen lise kimya öğretmeni Walter White, ailesinin geleceğini güvence altına almak için eski öğrencisi Jesse Pinkman ile metamfetamin üretip satmaya başlar.",
            genres = listOf("Suç", "Dram", "Gerilim"),
            trailerUrl = "https://www.youtube.com/watch?v=HhesaQXLuRY",
            cast = listOf(
                CastMember("Bryan Cranston", "Walter White", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop"),
                CastMember("Aaron Paul", "Jesse Pinkman", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop")
            ),
            watchStatus = "WATCHED",
            userRating = 10,
            userNotes = "Televizyon tarihinin en kusursuz finallerinden birine sahip şaheser.",
            watchedEpisodes = 62,
            totalEpisodes = 62
        ),
        MediaItem(
            id = "severance-tv",
            title = "Severance",
            originalTitle = "Severance",
            type = "TV",
            year = 2022,
            runtime = "2 Sezon (19 Bölüm)",
            rating = 8.7f,
            posterUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600&auto=format&fit=crop",
            backdropUrl = "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=1200&auto=format&fit=crop",
            overview = "Lumon Industries çalışanları, iş ve kişisel anılarını tıbbi operasyonla birbirinden ayıran 'Severance' prosedürüne katılırlar. İş benlikleri dış dünya hakkında hiçbir şey bilmez.",
            genres = listOf("Psikolojik", "Bilim Kurgu", "Gerilim"),
            trailerUrl = "https://www.youtube.com/watch?v=xEQP4VVuyrY",
            cast = listOf(
                CastMember("Adam Scott", "Mark Scout", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300&auto=format&fit=crop"),
                CastMember("Patricia Arquette", "Harmony Cobel", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300&auto=format&fit=crop")
            ),
            watchStatus = "WATCHING",
            userRating = 9,
            userNotes = "Beyin bükücü senaryo, beyaz ofis atmosferi büyüleyici derecede klostrofobik.",
            watchedEpisodes = 9,
            totalEpisodes = 19
        ),
        MediaItem(
            id = "dune2-movie",
            title = "Dune: Part Two",
            originalTitle = "Dune: Part Two",
            type = "MOVIE",
            year = 2024,
            runtime = "2sa 46dk",
            rating = 8.6f,
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop",
            overview = "Paul Atreides, ailesini yok eden komploculara karşı intikam yolunda Chani ve Fremen'lerle birleşir. Evrenin kaderi ile hayatının aşkı arasında seçim yapmak zorundadır.",
            genres = listOf("Bilim Kurgu", "Aksiyon", "Macera"),
            trailerUrl = "https://www.youtube.com/watch?v=Way9Dexny3w",
            cast = listOf(
                CastMember("Timothée Chalamet", "Paul Atreides", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop"),
                CastMember("Zendaya", "Chani", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop")
            ),
            watchStatus = "TO_WATCH",
            userRating = null,
            userNotes = "Sinematografisi müthiş.",
            watchedEpisodes = 0,
            totalEpisodes = 0
        ),
        MediaItem(
            id = "oppenheimer-movie",
            title = "Oppenheimer",
            originalTitle = "Oppenheimer",
            type = "MOVIE",
            year = 2023,
            runtime = "3sa 00dk",
            rating = 8.9f,
            posterUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=600&auto=format&fit=crop",
            backdropUrl = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=1200&auto=format&fit=crop",
            overview = "Manhattan Projesi'nin başına getirilen fizikçi J. Robert Oppenheimer'ın ilk atom bombasını geliştirme süreci ve sonrasındaki vicdani sorgulamaları.",
            genres = listOf("Tarih", "Dram", "Biyografi"),
            trailerUrl = "https://www.youtube.com/watch?v=uYPbbksJxIg",
            cast = listOf(
                CastMember("Cillian Murphy", "J. Robert Oppenheimer", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop"),
                CastMember("Robert Downey Jr.", "Lewis Strauss", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop")
            ),
            watchStatus = "WATCHED",
            userRating = 9,
            userNotes = "Ses tasarımı ve kurgusu büyüleyici.",
            watchedEpisodes = 0,
            totalEpisodes = 0
        ),
        MediaItem(
            id = "dark-tv",
            title = "Dark",
            originalTitle = "Dark",
            type = "TV",
            year = 2017,
            runtime = "3 Sezon (26 Bölüm)",
            rating = 8.7f,
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop",
            overview = "Almanya'nın Winden kasabasında iki çocuğun kaybolması, dört aile arasındaki karmaşık ilişkileri ve 33 yıllık zaman döngülerini ortaya çıkarır.",
            genres = listOf("Bilim Kurgu", "Gizem", "Gerilim"),
            trailerUrl = "https://www.youtube.com/watch?v=rrwyJJgBFyE",
            cast = listOf(
                CastMember("Louis Hofmann", "Jonas Kahnwald", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop")
            ),
            watchStatus = "TO_WATCH",
            userRating = null,
            userNotes = "Zaman yolculuğu kurgusunun zirvesi.",
            watchedEpisodes = 0,
            totalEpisodes = 26
        ),
        MediaItem(
            id = "prison-break-tv",
            title = "Prison Break",
            originalTitle = "Prison Break",
            type = "TV",
            year = 2005,
            runtime = "5 Sezon (90 Bölüm)",
            rating = 8.3f,
            posterUrl = "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=600&auto=format&fit=crop",
            backdropUrl = "https://images.unsplash.com/photo-1518173946687-a4c8a383392e?w=1200&auto=format&fit=crop",
            overview = "İdam cezasına çarptırılan suçsuz ağabeyini kaçırmak için dahi mühendis Michael Scofield, hapishanenin haritasını vücuduna dövme yaptırıp bilerek hapse girer.",
            genres = listOf("Suç", "Aksiyon", "Gerilim"),
            trailerUrl = "https://www.youtube.com/watch?v=AL9zLctDJaU",
            cast = listOf(
                CastMember("Wentworth Miller", "Michael Scofield", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop"),
                CastMember("Dominic Purcell", "Lincoln Burrows", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop")
            ),
            watchStatus = "WATCHED",
            userRating = 9,
            userNotes = "İlk sezonu dizi tarihinin en akıcı temposuna sahip.",
            watchedEpisodes = 90,
            totalEpisodes = 90
        ),
        MediaItem(
            id = "gibi-tv",
            title = "Gibi",
            originalTitle = "Gibi",
            type = "TV",
            year = 2021,
            runtime = "5 Sezon (52 Bölüm)",
            rating = 9.2f,
            posterUrl = "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=600&auto=format&fit=crop",
            backdropUrl = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=1200&auto=format&fit=crop",
            overview = "Yılmaz ve İlkkan, sürekli olarak hayatlarını altüst edecek küçük ve absürt olayların içinde kalırlar. Günlük hayatın basitleştirilmiş mantıksızlıklarını ince mizahla ele alan kült komedi dizisi.",
            genres = listOf("Komedi", "Absürt", "Dram"),
            trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            cast = listOf(
                CastMember("Feyyaz Yiğit", "Yılmaz", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop"),
                CastMember("Kıvanç Kılınç", "İlkkan", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop")
            ),
            watchStatus = "WATCHING",
            userRating = 10,
            userNotes = "Absürt mizahın harika bir örneği.",
            watchedEpisodes = 42,
            totalEpisodes = 52
        ),
        MediaItem(
            id = "sahsiyet-tv",
            title = "Şahsiyet",
            originalTitle = "Şahsiyet",
            type = "TV",
            year = 2018,
            runtime = "2 Sezon (22 Bölüm)",
            rating = 9.1f,
            posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&auto=format&fit=crop",
            backdropUrl = "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=1200&auto=format&fit=crop",
            overview = "Alzheimer teşhisi konan emekli adliye memuru Agâh Beyoğlu, unutmadan önce yıllardır ertelediği cinayet planını devreye sokar. Polis Nevra ise gizemli seri cinayetlerin izini sürer.",
            genres = listOf("Suç", "Gerilim", "Gizem"),
            trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            cast = listOf(
                CastMember("Haluk Bilginer", "Agâh Beyoğlu", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop"),
                CastMember("Cansu Dere", "Nevra Elmas", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&auto=format&fit=crop")
            ),
            watchStatus = "WATCHED",
            userRating = 10,
            userNotes = "Haluk Bilginer'e Emmy kazandıran muhteşem performans.",
            watchedEpisodes = 22,
            totalEpisodes = 22
        ),
        MediaItem(
            id = "kulup-tv",
            title = "Kulüp",
            originalTitle = "Kulüp",
            type = "TV",
            year = 2021,
            runtime = "2 Sezon (20 Bölüm)",
            rating = 8.8f,
            posterUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop",
            backdropUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=1200&auto=format&fit=crop",
            overview = "1950'lerin kozmopolit İstanbul'unda geçen hikâyede, hapisten çıkan Matilda, kızı Raşel ile yeniden bağ kurmaya çalışırken dönemin en ünlü gece kulübünde çalışmaya başlar.",
            genres = listOf("Dram", "Tarih", "Müzik"),
            trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            cast = listOf(
                CastMember("Gökçe Bahadır", "Matilda", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop"),
                CastMember("Barış Arduç", "İsmet", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=300&auto=format&fit=crop")
            ),
            watchStatus = "WATCHED",
            userRating = 9,
            userNotes = "Dönem atmosferi harika.",
            watchedEpisodes = 20,
            totalEpisodes = 20
        )
    )
}
