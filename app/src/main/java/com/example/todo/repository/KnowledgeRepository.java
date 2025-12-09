package com.example.todo.repository;

import com.example.todo.R;
import com.example.todo.model.KnowledgeArticle;
import com.example.todo.model.KnowledgeCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KnowledgeRepository {
    
    private static KnowledgeRepository instance;
    
    public static KnowledgeRepository getInstance() {
        if (instance == null) {
            instance = new KnowledgeRepository();
        }
        return instance;
    }
    
    public List<KnowledgeCategory> getAllCategories() {
        List<KnowledgeCategory> categories = new ArrayList<>();
        
        categories.add(new KnowledgeCategory(
            "qualification",
            "Kwalifikacja do przeszczepienia – krok po kroku",
            "",
            12,
            android.R.drawable.ic_menu_info_details,
            R.color.primary_burgundy,
            false
        ));
        
        categories.add(new KnowledgeCategory(
            "living_donation",
            "Żywe dawstwo",
            "",
            8,
            android.R.drawable.ic_menu_myplaces,
            R.color.info_blue,
            false
        ));
        
        categories.add(new KnowledgeCategory(
            "vaccinations",
            "Szczepienia",
            "",
            6,
            android.R.drawable.ic_menu_compass,
            R.color.status_orange,
            false
        ));
        
        categories.add(new KnowledgeCategory(
            "diet",
            "Dieta",
            "",
            9,
            android.R.drawable.ic_menu_agenda,
            R.color.status_green,
            false
        ));
        
        categories.add(new KnowledgeCategory(
            "patient_organizations",
            "Organizacje pacjentów",
            "",
            4,
            android.R.drawable.ic_menu_myplaces,
            R.color.primary_burgundy,
            false
        ));
        
        categories.add(new KnowledgeCategory(
            "sport_society",
            "Polskie Towarzystwo Sportu po Transplantacji",
            "",
            3,
            android.R.drawable.ic_menu_compass,
            R.color.info_blue,
            false
        ));
        
        categories.add(new KnowledgeCategory(
            "poltransplant_link",
            "Link do Poltransplant",
            "",
            0,
            android.R.drawable.ic_menu_share,
            R.color.primary_burgundy,
            true
        ));
        
        categories.add(new KnowledgeCategory(
            "other_links",
            "Inne przydatne linki",
            "",
            5,
            android.R.drawable.ic_menu_agenda,
            R.color.info_blue,
            false
        ));
        
        return categories;
    }
    
    public KnowledgeArticle getArticleByCategoryId(String categoryId) {
        switch (categoryId) {
            case "vaccinations":
                return createVaccinationsArticle();
            case "qualification":
                return createQualificationArticle();
            case "living_donation":
                return createLivingDonationArticle();
            case "diet":
                return createDietArticle();
            default:
                return createDefaultArticle(categoryId);
        }
    }
    
    private KnowledgeArticle createVaccinationsArticle() {
        List<String> importantPoints = Arrays.asList(
            "Skonsultuj harmonogram szczepień z lekarzem transplantologiem",
            "Zachowaj dokumentację wszystkich wykonanych szczepień",
            "Poinformuj zespół medyczny o wszelkich alergiach lub przeciwwskazaniach",
            "Regularnie sprawdzaj miano przeciwciał"
        );
        
        List<KnowledgeArticle.ExternalLink> links = Arrays.asList(
            new KnowledgeArticle.ExternalLink("Wytyczne KDIGO dotyczące szczepień", "https://kdigo.org"),
            new KnowledgeArticle.ExternalLink("Rekomendacje Polskiego Towarzystwa Transplantacyjnego", "https://ptt.org.pl"),
            new KnowledgeArticle.ExternalLink("Informacje o szczepieniach - WHO", "https://who.int")
        );
        
        String content = "Szczepienia są kluczowym elementem przygotowania do przeszczepienia nerki. " +
            "Właściwa ochrona immunologiczna przed zabiegiem może znacząco wpłynąć na bezpieczeństwo i skuteczność procedury.\n\n" +
            "Dlaczego szczepienia są ważne?\n\n" +
            "Pacjenci po przeszczepieniu otrzymują leki immunosupresyjne, które osłabiają układ odpornościowy. " +
            "Dlatego szczepienia przed transplantacją są kluczowe dla ochrony przed infekcjami.\n\n" +
            "Zalecane szczepienia:\n" +
            "• Grypa (szczepienie sezonowe, co roku)\n" +
            "• Pneumokoki (szczepionka koniugowana i polisacharydowa)\n" +
            "• Błonica, tężec, krztusiec (przypominające)\n" +
            "• Wirusowe zapalenie wątroby typu B\n" +
            "• Koronawirus SARS-CoV-2 (COVID-19)\n\n" +
            "Szczepionki przeciwwskazane po transplantacji:\n" +
            "Szczepionki żywe atenuowane (np. MMR, varicella) powinny być podane przed transplantacją, " +
            "ponieważ po przeszczepieniu mogą stanowić zagrożenie dla pacjenta z osłabionym układem odpornościowym.";
        
        String disclaimer = "Informacje zawarte w tym artykule mają charakter edukacyjny. " +
            "Zawsze konsultuj decyzje dotyczące szczepień z lekarzem prowadzącym.";
        
        return new KnowledgeArticle(
            "vaccinations_article",
            "vaccinations",
            "Szczepienia przed transplantacją nerki",
            "Szczepienia",
            5,
            "15.10.2024",
            content,
            importantPoints,
            links,
            disclaimer
        );
    }
    
    private KnowledgeArticle createQualificationArticle() {
        String content = "Proces kwalifikacji do przeszczepienia nerki składa się z kilku etapów...";
        return new KnowledgeArticle(
            "qualification_article",
            "qualification",
            "Kwalifikacja do przeszczepienia – krok po kroku",
            "Kwalifikacja",
            10,
            "01.11.2024",
            content,
            new ArrayList<>(),
            new ArrayList<>(),
            "Informacje mają charakter edukacyjny."
        );
    }
    
    private KnowledgeArticle createLivingDonationArticle() {
        String content = "Żywe dawstwo to forma przeszczepienia nerki od żywego dawcy...";
        return new KnowledgeArticle(
            "living_donation_article",
            "living_donation",
            "Żywe dawstwo",
            "Żywe dawstwo",
            8,
            "20.10.2024",
            content,
            new ArrayList<>(),
            new ArrayList<>(),
            "Informacje mają charakter edukacyjny."
        );
    }
    
    private KnowledgeArticle createDietArticle() {
        String content = "Prawidłowa dieta po przeszczepieniu nerki jest kluczowa dla utrzymania zdrowia...";
        return new KnowledgeArticle(
            "diet_article",
            "diet",
            "Dieta po przeszczepieniu nerki",
            "Dieta",
            7,
            "10.11.2024",
            content,
            new ArrayList<>(),
            new ArrayList<>(),
            "Informacje mają charakter edukacyjny."
        );
    }
    
    private KnowledgeArticle createDefaultArticle(String categoryId) {
        return new KnowledgeArticle(
            categoryId + "_article",
            categoryId,
            "Artykuł",
            "Ogólne",
            5,
            "01.01.2024",
            "Treść artykułu...",
            new ArrayList<>(),
            new ArrayList<>(),
            "Informacje mają charakter edukacyjny."
        );
    }
}

