package com.example.data

import java.net.URLEncoder

object CatalogData {
    private fun avatar(name: String): String {
        val enc = try { URLEncoder.encode(name, "UTF-8") } catch (e: Exception) { "Actor" }
        return "https://ui-avatars.com/api/?name=$enc&background=211B34&color=A855F7&size=200&bold=true"
    }

    val sampleCast = listOf(
        CastMember("Feyyaz Yiğit", "Yılmaz", avatar("Feyyaz Yiğit")),
        CastMember("Kıvanç Kılınç", "İlkkan", avatar("Kıvanç Kılınç")),
        CastMember("Ahmet Mümtaz Taylan", "Zafer", avatar("Ahmet Mümtaz Taylan")),
        CastMember("Haluk Bilginer", "Agâh Beyoğlu", avatar("Haluk Bilginer"))
    )

    val initialCatalog: List<MediaItem> = listOf(
        MediaItem(
            id = "superman-2025-movie",
            title = "Superman",
            originalTitle = "Superman",
            type = "MOVIE",
            year = 2025,
            runtime = "2sa 15dk",
            rating = 8.5f,
            posterUrl = "https://image.tmdb.org/t/p/w500/mndGq35yDCm8QSuTKRdF2o0KkB7.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/eGX66zonvc4bXg3rM08RUxdYSDx.jpg",
            overview = "Yıkılmış Krypton gezegeninden Dünya'ya gönderilen Kal-El, insanlığın adalet ve umut sembolü olarak yeni macerasına atılıyor.",
            genres = listOf("Aksiyon", "Bilim Kurgu", "Macera"),
            trailerUrl = "https://www.youtube.com/watch?v=Way9Dexny3w",
            cast = listOf(
                CastMember("David Corenswet", "Clark Kent / Superman", avatar("David Corenswet")),
                CastMember("Rachel Brosnahan", "Lois Lane", avatar("Rachel Brosnahan")),
                CastMember("Nicholas Hoult", "Lex Luthor", avatar("Nicholas Hoult"))
            )
        ),
        MediaItem(
            id = "f1-movie-2025",
            title = "F1 Filmi",
            originalTitle = "F1",
            type = "MOVIE",
            year = 2025,
            runtime = "2sa 20dk",
            rating = 8.4f,
            posterUrl = "https://image.tmdb.org/t/p/w500/m0XqulO3M01pmrhaUoJ8H55Gcg1.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/lkDYN0whyE82mcM20rwtwjbniKF.jpg",
            overview = "Eski efsanevi yarışçı Sonny Hayes, zor durumdaki bir Formula 1 takımına liderlik ederek pistlere geri dönüyor.",
            genres = listOf("Aksiyon", "Dram", "Spor"),
            trailerUrl = "https://www.youtube.com/watch?v=8mP1C0I8C4U",
            cast = listOf(
                CastMember("Brad Pitt", "Sonny Hayes", avatar("Brad Pitt")),
                CastMember("Damson Idris", "Joshua Pearce", avatar("Damson Idris")),
                CastMember("Javier Bardem", "Ruben", avatar("Javier Bardem"))
            )
        ),
        MediaItem(
            id = "severance-s2-tv",
            title = "Severance (Sezon 2)",
            originalTitle = "Severance",
            type = "TV",
            year = 2025,
            runtime = "2 Sezon (19 Bölüm)",
            rating = 8.9f,
            posterUrl = "https://image.tmdb.org/t/p/w500/pPHpeI2X1qEd1CS1SeyrdhZ4qnT.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/ixgFmf1X59PUZam2qbAfskx2gQr.jpg",
            overview = "Lumon Industries katlarındaki sir perdesi aralanıyor. Mark ve arkadaşları ameliyatlı zihin ayrıştırmasının ardındaki karanlık gerçekleri ortaya çıkarmak için direnişe geçiyor.",
            genres = listOf("Psikolojik", "Bilim Kurgu", "Gerilim"),
            trailerUrl = "https://www.youtube.com/watch?v=xEQP4VVuyrY",
            cast = listOf(
                CastMember("Adam Scott", "Mark Scout", avatar("Adam Scott")),
                CastMember("Patricia Arquette", "Harmony Cobel", avatar("Patricia Arquette")),
                CastMember("Britt Lower", "Helly R.", avatar("Britt Lower"))
            ),
            totalEpisodes = 19
        ),
        MediaItem(
            id = "avatar-3-movie",
            title = "Avatar: Ateş ve Kül",
            originalTitle = "Avatar: Fire and Ash",
            type = "MOVIE",
            year = 2025,
            runtime = "3sa 10dk",
            rating = 8.6f,
            posterUrl = "https://image.tmdb.org/t/p/w500/8kknNk7PbOcDUXynUZYZ7EHnKAA.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/sdZSjtGUTSN8B3al5o0f2WoQfQQ.jpg",
            overview = "Pandora gezegenindeki sulardan sonra volkanik bölgelere adım atan Sully ailesi, Na'vi kültürünün karanlık tarafını temsil eden Ateş Halkı ile tanışıyor.",
            genres = listOf("Bilim Kurgu", "Aksiyon", "Macera"),
            trailerUrl = "https://www.youtube.com/watch?v=d9MyW72ELq0",
            cast = listOf(
                CastMember("Sam Worthington", "Jake Sully", avatar("Sam Worthington")),
                CastMember("Zoe Saldaña", "Neytiri", avatar("Zoe Saldaña")),
                CastMember("Sigourney Weaver", "Kiri", avatar("Sigourney Weaver"))
            )
        ),
        MediaItem(
            id = "mi-8-movie",
            title = "Mission: Impossible - Son Hesaplaşma",
            originalTitle = "Mission: Impossible - The Final Reckoning",
            type = "MOVIE",
            year = 2025,
            runtime = "2sa 45dk",
            rating = 8.3f,
            posterUrl = "https://image.tmdb.org/t/p/w500/d2HIdVIzuIUgIBxL4N0C9XeNeSX.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/538U9snNc2fpnOmYXAPUh3zn31H.jpg",
            overview = "Ethan Hunt ve IMF ekibi, dünyayı tehdit eden yapay zeka Entity'yi kalıcı olarak yok etmek için son ve en tehlikeli görevlerine çıkıyor.",
            genres = listOf("Aksiyon", "Gerilim", "Macera"),
            trailerUrl = "https://www.youtube.com/watch?v=NOhDyR-117Q",
            cast = listOf(
                CastMember("Tom Cruise", "Ethan Hunt", avatar("Tom Cruise")),
                CastMember("Hayley Atwell", "Grace", avatar("Hayley Atwell")),
                CastMember("Ving Rhames", "Luther Stickell", avatar("Ving Rhames"))
            )
        ),
        MediaItem(
            id = "prens-s3-tv",
            title = "Prens (Sezon 3)",
            originalTitle = "Prens",
            type = "TV",
            year = 2025,
            runtime = "3 Sezon (22 Bölüm)",
            rating = 9.1f,
            posterUrl = "https://image.tmdb.org/t/p/w500/oDtYdl5i8rRLmB9xAU6VOwHKjMQ.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/88DMFlyPDuyUeFrUDswbWSV2CQg.jpg",
            overview = "Bongomia ülkesinde entrikalar bitmiyor! Ortanca Prens, yeni krallıklar ile ittifak kurmaya çalışırken Bongomia'yı yine birbirine katıyor.",
            genres = listOf("Komedi", "Tarih", "Absürt"),
            trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            cast = listOf(
                CastMember("Giray Altınok", "Prens", avatar("Giray Altınok")),
                CastMember("Ceyda Düvenci", "Kraliçe Sivrikaya", avatar("Ceyda Düvenci")),
                CastMember("Serdar Orçin", "Kaptan", avatar("Serdar Orçin"))
            ),
            totalEpisodes = 22
        ),
        MediaItem(
            id = "gibi-s6-tv",
            title = "Gibi (Sezon 6)",
            originalTitle = "Gibi",
            type = "TV",
            year = 2025,
            runtime = "6 Sezon (60 Bölüm)",
            rating = 9.3f,
            posterUrl = "https://image.tmdb.org/t/p/w500/sOzTUfTc1djfZM46PgsjuOjs5U1.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/p7VgbdDN2LK41JTavzJ9x3i4ktE.jpg",
            overview = "Yılmaz, İlkkan ve Ersoy 6. sezonda da hayatın absürt kırılmalarında savrulmaya, küçücük insan ilişkilerini felsefi çıkmazlara dönüştürmeye devam ediyor.",
            genres = listOf("Komedi", "Absürt", "Dram"),
            trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            cast = listOf(
                CastMember("Feyyaz Yiğit", "Yılmaz", avatar("Feyyaz Yiğit")),
                CastMember("Kıvanç Kılınç", "İlkkan", avatar("Kıvanç Kılınç")),
                CastMember("Ahmet Mümtaz Taylan", "Zafer", avatar("Ahmet Mümtaz Taylan"))
            ),
            totalEpisodes = 60
        ),
        MediaItem(
            id = "dune2-movie",
            title = "Dune: Part Two",
            originalTitle = "Dune: Part Two",
            type = "MOVIE",
            year = 2024,
            runtime = "2sa 46dk",
            rating = 8.6f,
            posterUrl = "https://image.tmdb.org/t/p/w500/tihf8Trht9zP3scmUQfvGlAY9FU.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/eZ239CUp1d6OryZEBPnO2n87gMG.jpg",
            overview = "Paul Atreides, Arrakis gezegeni için mücadeleye devam ediyor ve Fremen halkının liderliğini üstleniyor. Paul, Harkonnen ailesinin saldırısından kurtulduktan sonra Fremenlerle birlikte imparatorluğa karşı savaşır.",
            genres = listOf("Bilim Kurgu", "Aksiyon", "Macera"),
            trailerUrl = "https://www.youtube.com/watch?v=Way9Dexny3w",
            cast = listOf(
                CastMember("Timothée Chalamet", "Paul Atreides", avatar("Timothée Chalamet")),
                CastMember("Zendaya", "Chani", avatar("Zendaya")),
                CastMember("Rebecca Ferguson", "Lady Jessica", avatar("Rebecca Ferguson")),
                CastMember("Javier Bardem", "Stilgar", avatar("Javier Bardem"))
            )
        ),
        MediaItem(
            id = "shogun-tv",
            title = "Shōgun",
            originalTitle = "Shōgun",
            type = "TV",
            year = 2024,
            runtime = "1 Sezon (10 Bölüm)",
            rating = 8.8f,
            posterUrl = "https://image.tmdb.org/t/p/w500/7O4iVfOMQmdCSxhOg1WnzG1AgYT.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/bwSmgmd90hCWwqOKQYTEraeOZhJ.jpg",
            overview = "1600'lerin Feodal Japonya'sında geçen epik güç mücadelesinde Lord Yoshii Toranaga, iç savaşın eşiğinde hayatı ve klanının geleceği için düşmanlarına karşı savaşır.",
            genres = listOf("Dram", "Tarih", "Aksiyon"),
            trailerUrl = "https://www.youtube.com/watch?v=yAN5uspO_hk",
            cast = listOf(
                CastMember("Hiroyuki Sanada", "Lord Yoshii Toranaga", avatar("Hiroyuki Sanada")),
                CastMember("Cosmo Jarvis", "John Blackthorne", avatar("Cosmo Jarvis")),
                CastMember("Anna Sawai", "Toda Mariko", avatar("Anna Sawai"))
            ),
            totalEpisodes = 10
        ),
        MediaItem(
            id = "penguin-tv",
            title = "The Penguin",
            originalTitle = "The Penguin",
            type = "TV",
            year = 2024,
            runtime = "1 Sezon (8 Bölüm)",
            rating = 8.7f,
            posterUrl = "https://image.tmdb.org/t/p/w500/8LqwJMl45mFVQQcJmrZ3WvqvYNe.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/4TdmuuwiIiKw3JOjIuhdgYxRXnN.jpg",
            overview = "Oswald 'Penguen' Cobblepot'un, Carmine Falcone'un ölümünün ardından Gotham Şehri'nin yeraltı suç dünyasında iktidarı ele geçirme mücadelesi.",
            genres = listOf("Suç", "Dram", "Gerilim"),
            trailerUrl = "https://www.youtube.com/watch?v=sfM7_JLk-84",
            cast = listOf(
                CastMember("Colin Farrell", "Oz Cobb / Penguen", avatar("Colin Farrell")),
                CastMember("Cristin Milioti", "Sofia Falcone", avatar("Cristin Milioti"))
            ),
            totalEpisodes = 8
        ),
        MediaItem(
            id = "fallout-tv",
            title = "Fallout",
            originalTitle = "Fallout",
            type = "TV",
            year = 2024,
            runtime = "1 Sezon (8 Bölüm)",
            rating = 8.5f,
            posterUrl = "https://image.tmdb.org/t/p/w500/c15BtJxCXMrISLVmysdsnZUPQft.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/coaPCIqQBPUZsOnJcWZxhaORcDT.jpg",
            overview = "Nükleer kıyametten 200 yıl sonra lüks sığınak sakini Lucy, tehlikeli yüzey dünyasına adım atarak Los Angeles çorak topraklarında babasını aramaya başlar.",
            genres = listOf("Bilim Kurgu", "Aksiyon", "Macera"),
            trailerUrl = "https://www.youtube.com/watch?v=V-mugKDQDlg",
            cast = listOf(
                CastMember("Ella Purnell", "Lucy MacLean", avatar("Ella Purnell")),
                CastMember("Walton Goggins", "The Ghoul / Cooper", avatar("Walton Goggins")),
                CastMember("Aaron Moten", "Maximus", avatar("Aaron Moten"))
            ),
            totalEpisodes = 8
        ),
        MediaItem(
            id = "deadpool-wolverine-movie",
            title = "Deadpool & Wolverine",
            originalTitle = "Deadpool & Wolverine",
            type = "MOVIE",
            year = 2024,
            runtime = "2sa 08dk",
            rating = 7.9f,
            posterUrl = "https://image.tmdb.org/t/p/w500/fVr2X3jnoeLuZ2v0L1O8MOdOiSz.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/by8z9Fe8y7p4jo2YlW2SZDnptyT.jpg",
            overview = "Sivil hayata uyum sağlamaya çalışan Wade Wilson, evreni tehdit eden büyük bir tehlike karşısında isteksiz Wolverine ile güçlerini birleştirmek zorunda kalır.",
            genres = listOf("Aksiyon", "Komedi", "Bilim Kurgu"),
            trailerUrl = "https://www.youtube.com/watch?v=73_1biulkYk",
            cast = listOf(
                CastMember("Ryan Reynolds", "Wade Wilson / Deadpool", avatar("Ryan Reynolds")),
                CastMember("Hugh Jackman", "Logan / Wolverine", avatar("Hugh Jackman")),
                CastMember("Emma Corrin", "Cassandra Nova", avatar("Emma Corrin"))
            )
        ),
        MediaItem(
            id = "inside-out-2-movie",
            title = "Ters Yüz 2",
            originalTitle = "Inside Out 2",
            type = "MOVIE",
            year = 2024,
            runtime = "1sa 36dk",
            rating = 8.0f,
            posterUrl = "https://image.tmdb.org/t/p/w500/xYqeUheNCep7ll9AotOcclGhP0X.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/p5ozvmdgsmbWe0H8Xk7Rc8SCwAB.jpg",
            overview = "Ergenliğe adım atan Riley'nin zihninde sürpriz yeni Duygular belirir! Neşe, Üzüntü, Öfke, Korku ve Tiksinti, Kaygı ve ekibinin gelişiyle düzeni korumaya çalışır.",
            genres = listOf("Animasyon", "Komedi", "Aile"),
            trailerUrl = "https://www.youtube.com/watch?v=LEjhY15eCx0",
            cast = listOf(
                CastMember("Amy Poehler", "Joy", avatar("Amy Poehler")),
                CastMember("Maya Hawke", "Anxiety", avatar("Maya Hawke")),
                CastMember("Kensington Tallman", "Riley", avatar("Kensington Tallman"))
            )
        ),
        MediaItem(
            id = "gladiator-2-movie",
            title = "Gladyatör II",
            originalTitle = "Gladiator II",
            type = "MOVIE",
            year = 2024,
            runtime = "2sa 28dk",
            rating = 7.2f,
            posterUrl = "https://image.tmdb.org/t/p/w500/dARTCpnHY0R0B28j5q5ynq7erua.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/tOqIwliWMovSIZ9DyvHcHI7p2im.jpg",
            overview = "Maximus'un ölümünden yıllar sonra Lucius, zalim Roma imparatorlarının yönetiminde Kolezyum arenasının ölümcül dövüşlerinde onuru için savaşır.",
            genres = listOf("Aksiyon", "Dram", "Tarih"),
            trailerUrl = "https://www.youtube.com/watch?v=4rgIUya651E",
            cast = listOf(
                CastMember("Paul Mescal", "Lucius", avatar("Paul Mescal")),
                CastMember("Pedro Pascal", "Marcus Acacius", avatar("Pedro Pascal")),
                CastMember("Denzel Washington", "Macrinus", avatar("Denzel Washington"))
            )
        ),
        MediaItem(
            id = "prens-tv",
            title = "Prens",
            originalTitle = "Prens",
            type = "TV",
            year = 2023,
            runtime = "2 Sezon (15 Bölüm)",
            rating = 8.9f,
            posterUrl = "https://image.tmdb.org/t/p/w500/oDtYdl5i8rRLmB9xAU6VOwHKjMQ.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/88DMFlyPDuyUeFrUDswbWSV2CQg.jpg",
            overview = "Kimsenin umrunda olmayan Bongomia ülkesinde, kralın ölümü üzerine hiç kimsenin ciddiye almadığı Ortanca Prens'in tahta geçmesiyle başlayan kahkaha dolu olaylar.",
            genres = listOf("Komedi", "Tarih", "Absürt"),
            trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            cast = listOf(
                CastMember("Giray Altınok", "Prens", avatar("Giray Altınok")),
                CastMember("Ceyda Düvenci", "Kraliçe Sivrikaya", avatar("Ceyda Düvenci")),
                CastMember("Serdar Orçin", "Kaptan", avatar("Serdar Orçin"))
            ),
            totalEpisodes = 15
        ),
        MediaItem(
            id = "bahar-tv",
            title = "Bahar",
            originalTitle = "Bahar",
            type = "TV",
            year = 2024,
            runtime = "2 Sezon",
            rating = 7.8f,
            posterUrl = "https://image.tmdb.org/t/p/w500/Adjn7rsb1picDMUcYNjEDobhvAk.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/6dXyUXZ4HsY6M3Tae9e0KZsFCo1.jpg",
            overview = "Ölümle burun buruna geldikten sonra hayatını değiştirmeye karar veren Bahar'ın, yıllar sonra doktorluk mesleğine dönme ve ailesinde sınırları yeniden çizme mücadelesi.",
            genres = listOf("Dram", "Komedi"),
            trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            cast = listOf(
                CastMember("Demet Evgar", "Bahar Yavuzoğlu", avatar("Demet Evgar")),
                CastMember("Buğra Gülsoy", "Evren Yalkın", avatar("Buğra Gülsoy")),
                CastMember("Mehmet Yılmaz Ak", "Timur Yavuzoğlu", avatar("Mehmet Yılmaz Ak"))
            )
        ),
        MediaItem(
            id = "oppenheimer-movie",
            title = "Oppenheimer",
            originalTitle = "Oppenheimer",
            type = "MOVIE",
            year = 2023,
            runtime = "3sa 00dk",
            rating = 8.9f,
            posterUrl = "https://image.tmdb.org/t/p/w500/mmZi0tyPFfbcCqEsJIPxVldCPOL.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/neeNHeXjMF5fXoCJRsOmkNGC7q.jpg",
            overview = "Manhattan Projesi'nin başına getirilen fizikçi J. Robert Oppenheimer'ın ilk atom bombasını geliştirme süreci ve sonrasındaki vicdani sorgulamaları.",
            genres = listOf("Tarih", "Dram", "Biyografi"),
            trailerUrl = "https://www.youtube.com/watch?v=uYPbbksJxIg",
            cast = listOf(
                CastMember("Cillian Murphy", "J. Robert Oppenheimer", avatar("Cillian Murphy")),
                CastMember("Robert Downey Jr.", "Lewis Strauss", avatar("Robert Downey Jr.")),
                CastMember("Emily Blunt", "Katherine Oppenheimer", avatar("Emily Blunt"))
            )
        ),
        MediaItem(
            id = "gibi-tv",
            title = "Gibi",
            originalTitle = "Gibi",
            type = "TV",
            year = 2021,
            runtime = "5 Sezon (52 Bölüm)",
            rating = 9.2f,
            posterUrl = "https://image.tmdb.org/t/p/w500/sOzTUfTc1djfZM46PgsjuOjs5U1.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/p7VgbdDN2LK41JTavzJ9x3i4ktE.jpg",
            overview = "Yılmaz, İlkkan ve Ersoy, sürekli olarak hayatlarını altüst edecek küçük ve absürt olayların içinde kalırlar. Günlük hayatın basitleştirilmiş mantıksızlıklarını ince mizahla ele alan kült komedi dizisi.",
            genres = listOf("Komedi", "Absürt", "Dram"),
            trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            cast = listOf(
                CastMember("Feyyaz Yiğit", "Yılmaz", avatar("Feyyaz Yiğit")),
                CastMember("Kıvanç Kılınç", "İlkkan", avatar("Kıvanç Kılınç")),
                CastMember("Ahmet Mümtaz Taylan", "Zafer", avatar("Ahmet Mümtaz Taylan"))
            ),
            totalEpisodes = 52
        ),
        MediaItem(
            id = "interstellar-movie",
            title = "Interstellar",
            originalTitle = "Interstellar",
            type = "MOVIE",
            year = 2014,
            runtime = "2sa 49dk",
            rating = 8.7f,
            posterUrl = "https://image.tmdb.org/t/p/w500/xbiycuc84TrieEWwkkuH2hoEa9S.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/5XNQBqnBwPA9yT0jZ0p3s8bbLh0.jpg",
            overview = "Dünyanın kuraklık ve kıtlıkla yüzleştiği gelecekte, insanlığın hayatta kalabilmesi için solucan deliğinden geçerek yeni yaşanabilir gezegenler arayan astronot grubunun epik yolculuğu.",
            genres = listOf("Bilim Kurgu", "Macera", "Dram"),
            trailerUrl = "https://www.youtube.com/watch?v=zSWdZVtXT7E",
            cast = listOf(
                CastMember("Matthew McConaughey", "Cooper", avatar("Matthew McConaughey")),
                CastMember("Anne Hathaway", "Brand", avatar("Anne Hathaway")),
                CastMember("Jessica Chastain", "Murph", avatar("Jessica Chastain")),
                CastMember("Michael Caine", "Prof. Brand", avatar("Michael Caine"))
            )
        ),
        MediaItem(
            id = "breaking-bad-tv",
            title = "Breaking Bad",
            originalTitle = "Breaking Bad",
            type = "TV",
            year = 2008,
            runtime = "5 Sezon (62 Bölüm)",
            rating = 9.5f,
            posterUrl = "https://image.tmdb.org/t/p/w500/anFx9aTOOYqgS3v7x3R84Kz67ly.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/tsRy63Mu5cu8etL1X7ZLyf7UP1M.jpg",
            overview = "Kanser olduğunu öğrenen lise kimya öğretmeni Walter White, ailesinin geleceğini güvence altına almak için eski öğrencisi Jesse Pinkman ile metamfetamin üretip satmaya başlar.",
            genres = listOf("Suç", "Dram", "Gerilim"),
            trailerUrl = "https://www.youtube.com/watch?v=HhesaQXLuRY",
            cast = listOf(
                CastMember("Bryan Cranston", "Walter White", avatar("Bryan Cranston")),
                CastMember("Aaron Paul", "Jesse Pinkman", avatar("Aaron Paul")),
                CastMember("Anna Gunn", "Skyler White", avatar("Anna Gunn")),
                CastMember("Bob Odenkirk", "Saul Goodman", avatar("Bob Odenkirk"))
            ),
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
            posterUrl = "https://image.tmdb.org/t/p/w500/pPHpeI2X1qEd1CS1SeyrdhZ4qnT.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/ixgFmf1X59PUZam2qbAfskx2gQr.jpg",
            overview = "Lumon Industries çalışanları, iş ve kişisel anılarını tıbbi operasyonla birbirinden ayıran 'Severance' prosedürüne katırlar. İş benlikleri dış dünya hakkında hiçbir şey bilmez.",
            genres = listOf("Psikolojik", "Bilim Kurgu", "Gerilim"),
            trailerUrl = "https://www.youtube.com/watch?v=xEQP4VVuyrY",
            cast = listOf(
                CastMember("Adam Scott", "Mark Scout", avatar("Adam Scott")),
                CastMember("Patricia Arquette", "Harmony Cobel", avatar("Patricia Arquette")),
                CastMember("Britt Lower", "Helly R.", avatar("Britt Lower"))
            ),
            totalEpisodes = 19
        ),
        MediaItem(
            id = "dark-tv",
            title = "Dark",
            originalTitle = "Dark",
            type = "TV",
            year = 2017,
            runtime = "3 Sezon (26 Bölüm)",
            rating = 8.7f,
            posterUrl = "https://image.tmdb.org/t/p/w500/zAqWleNycZ86klkXJVY1OPpTdyA.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/3jDXL4Xvj3AzDOF6UH1xeyHW8MH.jpg",
            overview = "Almanya'nın Winden kasabasında iki çocuğun kaybolması, dört aile arasındaki karmaşık ilişkileri ve 33 yıllık zaman döngülerini ortaya çıkarır.",
            genres = listOf("Bilim Kurgu", "Gizem", "Gerilim"),
            trailerUrl = "https://www.youtube.com/watch?v=rrwyJJgBFyE",
            cast = listOf(
                CastMember("Louis Hofmann", "Jonas Kahnwald", avatar("Louis Hofmann")),
                CastMember("Oliver Masucci", "Ulrich Nielsen", avatar("Oliver Masucci"))
            ),
            totalEpisodes = 26
        ),
        MediaItem(
            id = "sahsiyet-tv",
            title = "Şahsiyet",
            originalTitle = "Şahsiyet",
            type = "TV",
            year = 2018,
            runtime = "2 Sezon (22 Bölüm)",
            rating = 9.1f,
            posterUrl = "https://image.tmdb.org/t/p/w500/nC68Pf7jnddVcZsPuzAnYUHJqff.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/3MfziPWYvHaYwTdi5jxkEscjHVw.jpg",
            overview = "Alzheimer teşhisi konan emekli adliye memuru Agâh Beyoğlu, unutmadan önce yıllardır ertelediği cinayet planını devreye sokar. Polis Nevra ise gizemli seri cinayetlerin izini sürer.",
            genres = listOf("Suç", "Gerilim", "Gizem"),
            trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            cast = listOf(
                CastMember("Haluk Bilginer", "Agâh Beyoğlu", avatar("Haluk Bilginer")),
                CastMember("Cansu Dere", "Nevra Elmas", avatar("Cansu Dere")),
                CastMember("Metin Akdülger", "Ateş Arbay", avatar("Metin Akdülger"))
            ),
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
            posterUrl = "https://image.tmdb.org/t/p/w500/jHgMj6mtMHEBrFhmZaQCGFGYnFx.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/AwEQ9wAOs1ZbvhpO7XxpGQHYvx5.jpg",
            overview = "1950'lerin kozmopolit İstanbul'unda geçen hikâyede, hapisten çıkan Matilda, kızı Raşel ile yeniden bağ kurmaya çalışırken dönemin en ünlü gece kulübünde çalışmaya başlar.",
            genres = listOf("Dram", "Tarih"),
            trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            cast = listOf(
                CastMember("Gökçe Bahadır", "Matilda Baeva", avatar("Gökçe Bahadır")),
                CastMember("Barış Arduç", "İsmet Denizer", avatar("Barış Arduç")),
                CastMember("Salih Bademci", "Selim Songür", avatar("Salih Bademci"))
            ),
            totalEpisodes = 20
        ),
        MediaItem(
            id = "prison-break-tv",
            title = "Prison Break",
            originalTitle = "Prison Break",
            type = "TV",
            year = 2005,
            runtime = "5 Sezon (90 Bölüm)",
            rating = 8.3f,
            posterUrl = "https://image.tmdb.org/t/p/w500/wnmNPaLvhnMeOqnWlhNkYCZxtda.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w1280/n3Brk7roueE9HOwVmYlJx5j462g.jpg",
            overview = "İdam cezasına çarptırılan suçsuz ağabeyini kaçırmak için dahi mühendis Michael Scofield, hapishanenin haritasını vücuduna dövme yaptırıp bilerek hapse girer.",
            genres = listOf("Suç", "Aksiyon", "Gerilim"),
            trailerUrl = "https://www.youtube.com/watch?v=AL9zLctDJaU",
            cast = listOf(
                CastMember("Wentworth Miller", "Michael Scofield", avatar("Wentworth Miller")),
                CastMember("Dominic Purcell", "Lincoln Burrows", avatar("Dominic Purcell")),
                CastMember("Robert Knepper", "T-Bag", avatar("Robert Knepper"))
            ),
            totalEpisodes = 90
        )
    )
}

