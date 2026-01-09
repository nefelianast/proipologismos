package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


public class Comparisons {
    
    public static class ComparisonData {
        private final String categoryName;
        private final long year1Value;
        private final long year2Value;
        
        public ComparisonData(String categoryName, long year1Value, long year2Value) {
            this.categoryName = categoryName;
            this.year1Value = year1Value;
            this.year2Value = year2Value;
        }
        
        public String getCategoryName() {
            return categoryName;
        }
        
        public long getYear1Value() {
            return year1Value;
        }
        
        public long getYear2Value() {
            return year2Value;
        }
        
        public long getDifference() {
            return year2Value - year1Value;
        }
        
        public String getPercentageChangeAsString() {
            if (year1Value == 0) {
                return year2Value > 0 ? "Νέο" : "0.00%";
            }
            double change = ((double)(year2Value - year1Value) / year1Value) * 100.0;
            return String.format("%.2f%%", change);
        }
    }
    
    public static class ComparisonResults {
        private final Map<String, ComparisonData> revenues;
        private final Map<String, ComparisonData> expenses;
        private final Map<String, ComparisonData> administrations;
        private final Map<String, ComparisonData> ministries;
        private final long budgetResult1;
        private final long budgetResult2;
        private final long totalRevenueSummary1;
        private final long totalRevenueSummary2;
        private final long totalExpensesSummary1;
        private final long totalExpensesSummary2;
        private final long totalMinistriesSummary1;
        private final long totalMinistriesSummary2;
        private final long totalDASummary1;
        private final long totalDASummary2;
        
        public ComparisonResults(
                Map<String, ComparisonData> revenues,
                Map<String, ComparisonData> expenses,
                Map<String, ComparisonData> administrations,
                Map<String, ComparisonData> ministries,
                long budgetResult1, long budgetResult2,
                long totalRevenueSummary1, long totalRevenueSummary2,
                long totalExpensesSummary1, long totalExpensesSummary2,
                long totalMinistriesSummary1, long totalMinistriesSummary2,
                long totalDASummary1, long totalDASummary2) {
            this.revenues = revenues;
            this.expenses = expenses;
            this.administrations = administrations;
            this.ministries = ministries;
            this.budgetResult1 = budgetResult1;
            this.budgetResult2 = budgetResult2;
            this.totalRevenueSummary1 = totalRevenueSummary1;
            this.totalRevenueSummary2 = totalRevenueSummary2;
            this.totalExpensesSummary1 = totalExpensesSummary1;
            this.totalExpensesSummary2 = totalExpensesSummary2;
            this.totalMinistriesSummary1 = totalMinistriesSummary1;
            this.totalMinistriesSummary2 = totalMinistriesSummary2;
            this.totalDASummary1 = totalDASummary1;
            this.totalDASummary2 = totalDASummary2;
        }
        
        public Map<String, ComparisonData> getRevenues() {
            return revenues;
        }
        
        public Map<String, ComparisonData> getExpenses() {
            return expenses;
        }
        
        public Map<String, ComparisonData> getAdministrations() {
            return administrations;
        }
        
        public Map<String, ComparisonData> getMinistries() {
            return ministries;
        }
        
        public long getBudgetResult1() {
            return budgetResult1;
        }
        
        public long getBudgetResult2() {
            return budgetResult2;
        }
        
        public long getTotalRevenueSummary1() {
            return totalRevenueSummary1;
        }
        
        public long getTotalRevenueSummary2() {
            return totalRevenueSummary2;
        }
        
        public long getTotalExpensesSummary1() {
            return totalExpensesSummary1;
        }
        
        public long getTotalExpensesSummary2() {
            return totalExpensesSummary2;
        }
        
        public long getTotalMinistriesSummary1() {
            return totalMinistriesSummary1;
        }
        
        public long getTotalMinistriesSummary2() {
            return totalMinistriesSummary2;
        }
        
        public long getTotalDASummary1() {
            return totalDASummary1;
        }
        
        public long getTotalDASummary2() {
            return totalDASummary2;
        }
    }
    
    // New method for GUI - returns data instead of printing
    public ComparisonResults compareYears(int year1, int year2) throws SQLException {
        // Reuse the existing logic from comparisons_of_two_years
        // We'll extract the data reading part and return it
        String DB = "jdbc:sqlite:src/main/resources/database/BudgetData.db";
        Connection connection = DriverManager.getConnection(DB);
        Statement stmt = connection.createStatement();
        
        // Read all data (same as comparisons_of_two_years)
        String sql = "SELECT * FROM revenue_"+year1;
        ResultSet rs = stmt.executeQuery(sql);
        
        long total_revenue1 = 0, taxes1 = 0, social_contributions1 = 0, transfers1 = 0;
        long sales_of_goods_and_services1 = 0, other_current_revenue1 = 0;
        long fixed_assets1 = 0, debt_securities1 = 0, loans1 = 0;
        long equity_securities_and_fund_shares1 = 0, currency_and_deposit_liabilities1 = 0;
        long debt_securities_liabilities1 = 0, loans_liabilities1 = 0, financial_derivatives1 = 0;
        
        long total_revenue2 = 0, taxes2 = 0, social_contributions2 = 0, transfers2 = 0;
        long sales_of_goods_and_services2 = 0, other_current_revenue2 = 0;
        long fixed_assets2 = 0, debt_securities2 = 0, loans2 = 0;
        long equity_securities_and_fund_shares2 = 0, currency_and_deposit_liabilities2 = 0;
        long debt_securities_liabilities2 = 0, loans_liabilities2 = 0, financial_derivatives2 = 0;
        
        long total_expenses = 0, employee_benefits = 0, social_benefits = 0, transfersexp1 = 0;
        long purchases_of_goods_and_services = 0, subsidies = 0, interest = 0;
        long other_expenditures = 0, loansexp1 = 0, appropriations = 0;
        long fixed_assets = 0, valuables = 0;
        long equity_securities_and_fund_shares = 0, debt_securities_liabilities = 0, loans_liabilities = 0;
        
        long total_expenses2 = 0, employee_benefits2 = 0, social_benefits2 = 0, transfersexp2 = 0;
        long purchases_of_goods_and_services2 = 0, subsidies2 = 0, interest2 = 0;
        long other_expenditures2 = 0, loansexp2 = 0, appropriations2 = 0;
        long fixed_assets22 = 0, valuables2 = 0;
        long equity_securities_and_fund_shares22 = 0, debt_securities_liabilities22 = 0, loans_liabilities22 = 0;
        
        long total_da = 0, decentralized_administration_of_attica = 0;
        long decentralized_administration_of_thessaly_central_greece = 0;
        long decentralized_administration_of_epirus_western_macedonia = 0;
        long decentralized_administration_of_peloponnese_western_greece_and_ionian = 0;
        long decentralized_administration_of_aegean = 0;
        long decentralized_administration_of_crete = 0;
        long decentralized_administration_of_macedonia_thrace = 0;
        
        long total_da2 = 0, decentralized_administration_of_attica2 = 0;
        long decentralized_administration_of_thessaly_central_greece2 = 0;
        long decentralized_administration_of_epirus_western_macedonia2 = 0;
        long decentralized_administration_of_peloponnese_western_greece_and_ionian2 = 0;
        long decentralized_administration_of_aegean2 = 0;
        long decentralized_administration_of_crete2 = 0;
        long decentralized_administration_of_macedonia_thrace2 = 0;
        
        long total_ministries = 0, presidency_of_the_republic = 0, hellenic_parliament = 0;
        long presidency_of_the_government = 0, ministry_of_interior = 0;
        long ministry_of_foreign_affairs = 0, ministry_of_national_defence = 0;
        long ministry_of_health = 0, ministry_of_justice = 0;
        long ministry_of_education_religious_affairs_and_sports = 0, ministry_of_culture = 0;
        long ministry_of_national_economy_and_finance = 0;
        long ministry_of_agricultural_development_and_food = 0;
        long ministry_of_environment_and_energy = 0;
        long ministry_of_labor_and_social_security = 0;
        long ministry_of_social_cohesion_and_family = 0;
        long ministry_of_development = 0;
        long ministry_of_infrastructure_and_transport = 0;
        long ministry_of_maritime_affairs_and_insular_policy = 0;
        long ministry_of_tourism = 0, ministry_of_digital_governance = 0;
        long ministry_of_migration_and_asylum = 0, ministry_of_citizen_protection = 0;
        long ministry_of_climate_crisis_and_civil_protection = 0;
        
        long total_ministries2 = 0, presidency_of_the_republic2 = 0, hellenic_parliament2 = 0;
        long presidency_of_the_government2 = 0, ministry_of_interior2 = 0;
        long ministry_of_foreign_affairs2 = 0, ministry_of_national_defence2 = 0;
        long ministry_of_health2 = 0, ministry_of_justice2 = 0;
        long ministry_of_education_religious_affairs_and_sports2 = 0, ministry_of_culture2 = 0;
        long ministry_of_national_economy_and_finance2 = 0;
        long ministry_of_agricultural_development_and_food2 = 0;
        long ministry_of_environment_and_energy2 = 0;
        long ministry_of_labor_and_social_security2 = 0;
        long ministry_of_social_cohesion_and_family2 = 0;
        long ministry_of_development2 = 0;
        long ministry_of_infrastructure_and_transport2 = 0;
        long ministry_of_maritime_affairs_and_insular_policy2 = 0;
        long ministry_of_tourism2 = 0, ministry_of_digital_governance2 = 0;
        long ministry_of_migration_and_asylum2 = 0, ministry_of_citizen_protection2 = 0;
        long ministry_of_climate_crisis_and_civil_protection2 = 0;
        
        long budget_result = 0, total_revenue3 = 0, total_expenses3 = 0;
        long total_ministries3 = 0, total_da3 = 0;
        
        long budget_result2 = 0, total_revenue4 = 0, total_expenses4 = 0;
        long total_ministries4 = 0, total_da4 = 0;
        
        // Read year1 revenue data
        while (rs.next()) {
            total_revenue1 = rs.getLong("total_revenue");
            taxes1 = rs.getLong("taxes");
            social_contributions1 = rs.getLong("social_contributions");
            transfers1 = rs.getLong("transfers");
            sales_of_goods_and_services1 = rs.getLong("sales_of_goods_and_services");
            other_current_revenue1 = rs.getLong("other_current_revenue");
            fixed_assets1 = rs.getLong("fixed_assets");
            debt_securities1 = rs.getLong("debt_securities");
            if (year1 == 2025 || year1 == 2026) {
                loans1 = rs.getLong("loans");
            }
            equity_securities_and_fund_shares1 = rs.getLong("equity_securities_and_fund_shares");
            currency_and_deposit_liabilities1 = rs.getLong("currency_and_deposit_liabilities");
            debt_securities_liabilities1 = rs.getLong("debt_securities_liabilities");
            loans_liabilities1 = rs.getLong("loans_liabilities");
            financial_derivatives1 = rs.getLong("financial_derivatives");
        }
        rs.close();
        
        // Read year2 revenue data
        String sq2 = "SELECT * FROM revenue_"+year2;
        ResultSet rs2 = stmt.executeQuery(sq2);
        while (rs2.next()) {
            total_revenue2 = rs2.getLong("total_revenue");
            taxes2 = rs2.getLong("taxes");
            social_contributions2 = rs2.getLong("social_contributions");
            transfers2 = rs2.getLong("transfers");
            sales_of_goods_and_services2 = rs2.getLong("sales_of_goods_and_services");
            other_current_revenue2 = rs2.getLong("other_current_revenue");
            fixed_assets2 = rs2.getLong("fixed_assets");
            debt_securities2 = rs2.getLong("debt_securities");
            if (year2 == 2025 || year2 == 2026) {
                loans2 = rs2.getLong("loans");
            }
            equity_securities_and_fund_shares2 = rs2.getLong("equity_securities_and_fund_shares");
            currency_and_deposit_liabilities2 = rs2.getLong("currency_and_deposit_liabilities");
            debt_securities_liabilities2 = rs2.getLong("debt_securities_liabilities");
            loans_liabilities2 = rs2.getLong("loans_liabilities");
            financial_derivatives2 = rs2.getLong("financial_derivatives");
        }
        rs2.close();
        
        // Read year1 expense data
        String sq3 = "SELECT * FROM expenses_"+year1;
        ResultSet rs3 = stmt.executeQuery(sq3);
        while (rs3.next()) {
            total_expenses = rs3.getLong("total_expenses");
            employee_benefits = rs3.getLong("employee_benefits");
            social_benefits = rs3.getLong("social_benefits");
            transfersexp1 = rs3.getLong("transfers");
            purchases_of_goods_and_services = rs3.getLong("purchases_of_goods_and_services");
            subsidies = rs3.getLong("subsidies");
            interest = rs3.getLong("interest");
            other_expenditures = rs3.getLong("other_expenditures");
            loansexp1 = rs3.getLong("loans");
            appropriations = rs3.getLong("appropriations");
            fixed_assets = rs3.getLong("fixed_assets");
            valuables = rs3.getLong("valuables");
            equity_securities_and_fund_shares = rs3.getLong("equity_securities_and_fund_shares");
            debt_securities_liabilities = rs3.getLong("debt_securities_liabilities");
            loans_liabilities = rs3.getLong("loans_liabilities");
        }
        rs3.close();
        
        // Read year2 expense data
        String sq4 = "SELECT * FROM expenses_"+year2;
        ResultSet rs4 = stmt.executeQuery(sq4);
        while (rs4.next()) {
            total_expenses2 = rs4.getLong("total_expenses");
            employee_benefits2 = rs4.getLong("employee_benefits");
            social_benefits2 = rs4.getLong("social_benefits");
            transfersexp2 = rs4.getLong("transfers");
            purchases_of_goods_and_services2 = rs4.getLong("purchases_of_goods_and_services");
            subsidies2 = rs4.getLong("subsidies");
            interest2 = rs4.getLong("interest");
            other_expenditures2 = rs4.getLong("other_expenditures");
            loansexp2 = rs4.getLong("loans");
            appropriations2 = rs4.getLong("appropriations");
            fixed_assets22 = rs4.getLong("fixed_assets");
            valuables2 = rs4.getLong("valuables");
            equity_securities_and_fund_shares22 = rs4.getLong("equity_securities_and_fund_shares");
            debt_securities_liabilities22 = rs4.getLong("debt_securities_liabilities");
            loans_liabilities22 = rs4.getLong("loans_liabilities");
        }
        rs4.close();
        
        // Read year1 decentralized administrations
        String sq5 = "SELECT * FROM decentralized_administrations_"+year1;
        ResultSet rs5 = stmt.executeQuery(sq5);
        while (rs5.next()) {
            total_da = rs5.getLong("total_da");
            decentralized_administration_of_attica = rs5.getLong("decentralized_administration_of_attica");
            decentralized_administration_of_thessaly_central_greece = rs5.getLong("decentralized_administration_of_thessaly_central_greece");
            decentralized_administration_of_epirus_western_macedonia = rs5.getLong("decentralized_administration_of_epirus_western_macedonia");
            decentralized_administration_of_peloponnese_western_greece_and_ionian = rs5.getLong("decentralized_administration_of_peloponnese_western_greece_and_ionian");
            decentralized_administration_of_aegean = rs5.getLong("decentralized_administration_of_aegean");
            decentralized_administration_of_crete = rs5.getLong("decentralized_administration_of_crete");
            decentralized_administration_of_macedonia_thrace = rs5.getLong("decentralized_administration_of_macedonia_thrace");
        }
        rs5.close();
        
        // Read year2 decentralized administrations
        String sq6 = "SELECT * FROM decentralized_administrations_"+year2;
        ResultSet rs6 = stmt.executeQuery(sq6);
        while (rs6.next()) {
            total_da2 = rs6.getLong("total_da");
            decentralized_administration_of_attica2 = rs6.getLong("decentralized_administration_of_attica");
            decentralized_administration_of_thessaly_central_greece2 = rs6.getLong("decentralized_administration_of_thessaly_central_greece");
            decentralized_administration_of_epirus_western_macedonia2 = rs6.getLong("decentralized_administration_of_epirus_western_macedonia");
            decentralized_administration_of_peloponnese_western_greece_and_ionian2 = rs6.getLong("decentralized_administration_of_peloponnese_western_greece_and_ionian");
            decentralized_administration_of_aegean2 = rs6.getLong("decentralized_administration_of_aegean");
            decentralized_administration_of_crete2 = rs6.getLong("decentralized_administration_of_crete");
            decentralized_administration_of_macedonia_thrace2 = rs6.getLong("decentralized_administration_of_macedonia_thrace");
        }
        rs6.close();
        
        // Read year1 ministries
        String sq7 = "SELECT * FROM ministries_"+year1;
        ResultSet rs7 = stmt.executeQuery(sq7);
        while (rs7.next()) {
            total_ministries = rs7.getLong("total_ministries");
            presidency_of_the_republic = rs7.getLong("presidency_of_the_republic");
            hellenic_parliament = rs7.getLong("hellenic_parliament");
            presidency_of_the_government = rs7.getLong("presidency_of_the_government");
            ministry_of_interior = rs7.getLong("ministry_of_interior");
            ministry_of_foreign_affairs = rs7.getLong("ministry_of_foreign_affairs");
            ministry_of_national_defence = rs7.getLong("ministry_of_national_defence");
            ministry_of_health = rs7.getLong("ministry_of_health");
            ministry_of_justice = rs7.getLong("ministry_of_justice");
            ministry_of_education_religious_affairs_and_sports = rs7.getLong("ministry_of_education_religious_affairs_and_sports");
            ministry_of_culture = rs7.getLong("ministry_of_culture");
            ministry_of_national_economy_and_finance = rs7.getLong("ministry_of_national_economy_and_finance");
            ministry_of_agricultural_development_and_food = rs7.getLong("ministry_of_agricultural_development_and_food");
            ministry_of_environment_and_energy = rs7.getLong("ministry_of_environment_and_energy");
            ministry_of_labor_and_social_security = rs7.getLong("ministry_of_labor_and_social_security");
            if (year1 == 2023) {
                ministry_of_social_cohesion_and_family = 0;
            } else {
                ministry_of_social_cohesion_and_family = rs7.getLong("ministry_of_social_cohesion_and_family");
            }
            ministry_of_development = rs7.getLong("ministry_of_development");
            ministry_of_infrastructure_and_transport = rs7.getLong("ministry_of_infrastructure_and_transport");
            ministry_of_maritime_affairs_and_insular_policy = rs7.getLong("ministry_of_maritime_affairs_and_insular_policy");
            ministry_of_tourism = rs7.getLong("ministry_of_tourism");
            ministry_of_digital_governance = rs7.getLong("ministry_of_digital_governance");
            ministry_of_migration_and_asylum = rs7.getLong("ministry_of_migration_and_asylum");
            ministry_of_citizen_protection = rs7.getLong("ministry_of_citizen_protection");
            ministry_of_climate_crisis_and_civil_protection = rs7.getLong("ministry_of_climate_crisis_and_civil_protection");
        }
        rs7.close();
        
        // Read year2 ministries
        String sq8 = "SELECT * FROM ministries_"+year2;
        ResultSet rs8 = stmt.executeQuery(sq8);
        while (rs8.next()) {
            total_ministries2 = rs8.getLong("total_ministries");
            presidency_of_the_republic2 = rs8.getLong("presidency_of_the_republic");
            hellenic_parliament2 = rs8.getLong("hellenic_parliament");
            presidency_of_the_government2 = rs8.getLong("presidency_of_the_government");
            ministry_of_interior2 = rs8.getLong("ministry_of_interior");
            ministry_of_foreign_affairs2 = rs8.getLong("ministry_of_foreign_affairs");
            ministry_of_national_defence2 = rs8.getLong("ministry_of_national_defence");
            ministry_of_health2 = rs8.getLong("ministry_of_health");
            ministry_of_justice2 = rs8.getLong("ministry_of_justice");
            ministry_of_education_religious_affairs_and_sports2 = rs8.getLong("ministry_of_education_religious_affairs_and_sports");
            ministry_of_culture2 = rs8.getLong("ministry_of_culture");
            ministry_of_national_economy_and_finance2 = rs8.getLong("ministry_of_national_economy_and_finance");
            ministry_of_agricultural_development_and_food2 = rs8.getLong("ministry_of_agricultural_development_and_food");
            ministry_of_environment_and_energy2 = rs8.getLong("ministry_of_environment_and_energy");
            ministry_of_labor_and_social_security2 = rs8.getLong("ministry_of_labor_and_social_security");
            if (year2 == 2023) {
                ministry_of_social_cohesion_and_family2 = 0;
            } else {
                ministry_of_social_cohesion_and_family2 = rs8.getLong("ministry_of_social_cohesion_and_family");
            }
            ministry_of_development2 = rs8.getLong("ministry_of_development");
            ministry_of_infrastructure_and_transport2 = rs8.getLong("ministry_of_infrastructure_and_transport");
            ministry_of_maritime_affairs_and_insular_policy2 = rs8.getLong("ministry_of_maritime_affairs_and_insular_policy");
            ministry_of_tourism2 = rs8.getLong("ministry_of_tourism");
            ministry_of_digital_governance2 = rs8.getLong("ministry_of_digital_governance");
            ministry_of_migration_and_asylum2 = rs8.getLong("ministry_of_migration_and_asylum");
            ministry_of_citizen_protection2 = rs8.getLong("ministry_of_citizen_protection");
            ministry_of_climate_crisis_and_civil_protection2 = rs8.getLong("ministry_of_climate_crisis_and_civil_protection");
        }
        rs8.close();
        
        // Read budget summary for year1
        String sq9 = "SELECT * FROM budget_summary_"+year1;
        ResultSet rs9 = stmt.executeQuery(sq9);
        while (rs9.next()) {
            budget_result = rs9.getLong("budget_result");
            total_revenue3 = rs9.getLong("total_revenue");
            total_expenses3 = rs9.getLong("total_expenses");
            total_ministries3 = rs9.getLong("total_ministries");
            total_da3 = rs9.getLong("total_da");
        }
        rs9.close();
        
        // Read budget summary for year2
        String sq10 = "SELECT * FROM budget_summary_"+year2;
        ResultSet rs10 = stmt.executeQuery(sq10);
        while (rs10.next()) {
            budget_result2 = rs10.getLong("budget_result");
            total_revenue4 = rs10.getLong("total_revenue");
            total_expenses4 = rs10.getLong("total_expenses");
            total_ministries4 = rs10.getLong("total_ministries");
            total_da4 = rs10.getLong("total_da");
        }
        rs10.close();
        
        stmt.close();
        connection.close();
        
        // Build comparison data maps
        Map<String, ComparisonData> revenues = new HashMap<>();
        revenues.put("Total revenue", new ComparisonData("Total revenue", total_revenue1, total_revenue2));
        revenues.put("Taxes", new ComparisonData("Taxes", taxes1, taxes2));
        revenues.put("Social contributions", new ComparisonData("Social contributions", social_contributions1, social_contributions2));
        revenues.put("Transfers", new ComparisonData("Transfers", transfers1, transfers2));
        revenues.put("Sales of goods & services", new ComparisonData("Sales of goods & services", sales_of_goods_and_services1, sales_of_goods_and_services2));
        revenues.put("Other current revenue", new ComparisonData("Other current revenue", other_current_revenue1, other_current_revenue2));
        revenues.put("Fixed assets", new ComparisonData("Fixed assets", fixed_assets1, fixed_assets2));
        revenues.put("Debt securities", new ComparisonData("Debt securities", debt_securities1, debt_securities2));
        if (year1 == 2025 || year2 == 2025 || year1 == 2026 || year2 == 2026) {
            revenues.put("Loans", new ComparisonData("Loans", loans1, loans2));
        }
        revenues.put("Equity securities & fund shares", new ComparisonData("Equity securities & fund shares", equity_securities_and_fund_shares1, equity_securities_and_fund_shares2));
        revenues.put("Currency & deposits liabilities", new ComparisonData("Currency & deposits liabilities", currency_and_deposit_liabilities1, currency_and_deposit_liabilities2));
        revenues.put("Debt securities liabilities", new ComparisonData("Debt securities liabilities", debt_securities_liabilities1, debt_securities_liabilities2));
        revenues.put("Loans liabilities", new ComparisonData("Loans liabilities", loans_liabilities1, loans_liabilities2));
        revenues.put("Financial derivatives", new ComparisonData("Financial derivatives", financial_derivatives1, financial_derivatives2));
        
        Map<String, ComparisonData> expenses = new HashMap<>();
        expenses.put("Total expenses", new ComparisonData("Total expenses", total_expenses, total_expenses2));
        expenses.put("Employee benefits", new ComparisonData("Employee benefits", employee_benefits, employee_benefits2));
        expenses.put("Social benefits", new ComparisonData("Social benefits", social_benefits, social_benefits2));
        expenses.put("Transfers (expenses)", new ComparisonData("Transfers (expenses)", transfersexp1, transfersexp2));
        expenses.put("Purchases of goods & services", new ComparisonData("Purchases of goods & services", purchases_of_goods_and_services, purchases_of_goods_and_services2));
        expenses.put("Subsidies", new ComparisonData("Subsidies", subsidies, subsidies2));
        expenses.put("Interest", new ComparisonData("Interest", interest, interest2));
        expenses.put("Other expenditures", new ComparisonData("Other expenditures", other_expenditures, other_expenditures2));
        expenses.put("Loans (expenses)", new ComparisonData("Loans (expenses)", loansexp1, loansexp2));
        expenses.put("Appropriations", new ComparisonData("Appropriations", appropriations, appropriations2));
        expenses.put("Fixed assets (expenses)", new ComparisonData("Fixed assets (expenses)", fixed_assets, fixed_assets22));
        expenses.put("Valuables", new ComparisonData("Valuables", valuables, valuables2));
        expenses.put("Equity securities & fund shares (exp)", new ComparisonData("Equity securities & fund shares (exp)", equity_securities_and_fund_shares, equity_securities_and_fund_shares22));
        expenses.put("Debt securities liabilities (exp)", new ComparisonData("Debt securities liabilities (exp)", debt_securities_liabilities, debt_securities_liabilities22));
        expenses.put("Loans liabilities (exp)", new ComparisonData("Loans liabilities (exp)", loans_liabilities, loans_liabilities22));
        
        Map<String, ComparisonData> administrations = new HashMap<>();
        administrations.put("Total decentralized administrations", new ComparisonData("Total decentralized administrations", total_da, total_da2));
        administrations.put("Attica", new ComparisonData("Attica", decentralized_administration_of_attica, decentralized_administration_of_attica2));
        administrations.put("Thessaly & Central Greece", new ComparisonData("Thessaly & Central Greece", decentralized_administration_of_thessaly_central_greece, decentralized_administration_of_thessaly_central_greece2));
        administrations.put("Epirus & Western Macedonia", new ComparisonData("Epirus & Western Macedonia", decentralized_administration_of_epirus_western_macedonia, decentralized_administration_of_epirus_western_macedonia2));
        administrations.put("Peloponnese, Western Greece & Ionian", new ComparisonData("Peloponnese, Western Greece & Ionian", decentralized_administration_of_peloponnese_western_greece_and_ionian, decentralized_administration_of_peloponnese_western_greece_and_ionian2));
        administrations.put("Aegean", new ComparisonData("Aegean", decentralized_administration_of_aegean, decentralized_administration_of_aegean2));
        administrations.put("Crete", new ComparisonData("Crete", decentralized_administration_of_crete, decentralized_administration_of_crete2));
        administrations.put("Macedonia & Thrace", new ComparisonData("Macedonia & Thrace", decentralized_administration_of_macedonia_thrace, decentralized_administration_of_macedonia_thrace2));
        
        Map<String, ComparisonData> ministries = new HashMap<>();
        ministries.put("Total ministries", new ComparisonData("Total ministries", total_ministries, total_ministries2));
        ministries.put("Presidency of the Republic", new ComparisonData("Presidency of the Republic", presidency_of_the_republic, presidency_of_the_republic2));
        ministries.put("Hellenic Parliament", new ComparisonData("Hellenic Parliament", hellenic_parliament, hellenic_parliament2));
        ministries.put("Presidency of the Government", new ComparisonData("Presidency of the Government", presidency_of_the_government, presidency_of_the_government2));
        ministries.put("Ministry of Interior", new ComparisonData("Ministry of Interior", ministry_of_interior, ministry_of_interior2));
        ministries.put("Ministry of Foreign Affairs", new ComparisonData("Ministry of Foreign Affairs", ministry_of_foreign_affairs, ministry_of_foreign_affairs2));
        ministries.put("Ministry of National Defence", new ComparisonData("Ministry of National Defence", ministry_of_national_defence, ministry_of_national_defence2));
        ministries.put("Ministry of Health", new ComparisonData("Ministry of Health", ministry_of_health, ministry_of_health2));
        ministries.put("Ministry of Justice", new ComparisonData("Ministry of Justice", ministry_of_justice, ministry_of_justice2));
        ministries.put("Ministry of Education, Religious Affairs & Sports", new ComparisonData("Ministry of Education, Religious Affairs & Sports", ministry_of_education_religious_affairs_and_sports, ministry_of_education_religious_affairs_and_sports2));
        ministries.put("Ministry of Culture", new ComparisonData("Ministry of Culture", ministry_of_culture, ministry_of_culture2));
        ministries.put("Ministry of National Economy & Finance", new ComparisonData("Ministry of National Economy & Finance", ministry_of_national_economy_and_finance, ministry_of_national_economy_and_finance2));
        ministries.put("Ministry of Agricultural Development & Food", new ComparisonData("Ministry of Agricultural Development & Food", ministry_of_agricultural_development_and_food, ministry_of_agricultural_development_and_food2));
        ministries.put("Ministry of Environment & Energy", new ComparisonData("Ministry of Environment & Energy", ministry_of_environment_and_energy, ministry_of_environment_and_energy2));
        ministries.put("Ministry of Labor & Social Security", new ComparisonData("Ministry of Labor & Social Security", ministry_of_labor_and_social_security, ministry_of_labor_and_social_security2));
        ministries.put("Ministry of Social Cohesion & Family", new ComparisonData("Ministry of Social Cohesion & Family", ministry_of_social_cohesion_and_family, ministry_of_social_cohesion_and_family2));
        ministries.put("Ministry of Development", new ComparisonData("Ministry of Development", ministry_of_development, ministry_of_development2));
        ministries.put("Ministry of Infrastructure & Transport", new ComparisonData("Ministry of Infrastructure & Transport", ministry_of_infrastructure_and_transport, ministry_of_infrastructure_and_transport2));
        ministries.put("Ministry of Maritime Affairs & Insular Policy", new ComparisonData("Ministry of Maritime Affairs & Insular Policy", ministry_of_maritime_affairs_and_insular_policy, ministry_of_maritime_affairs_and_insular_policy2));
        ministries.put("Ministry of Tourism", new ComparisonData("Ministry of Tourism", ministry_of_tourism, ministry_of_tourism2));
        ministries.put("Ministry of Digital Governance", new ComparisonData("Ministry of Digital Governance", ministry_of_digital_governance, ministry_of_digital_governance2));
        ministries.put("Ministry of Migration & Asylum", new ComparisonData("Ministry of Migration & Asylum", ministry_of_migration_and_asylum, ministry_of_migration_and_asylum2));
        ministries.put("Ministry of Citizen Protection", new ComparisonData("Ministry of Citizen Protection", ministry_of_citizen_protection, ministry_of_citizen_protection2));
        ministries.put("Ministry of Climate Crisis & Civil Protection", new ComparisonData("Ministry of Climate Crisis & Civil Protection", ministry_of_climate_crisis_and_civil_protection, ministry_of_climate_crisis_and_civil_protection2));
        
        return new ComparisonResults(
                revenues,
                expenses,
                administrations,
                ministries,
                budget_result, budget_result2,
                total_revenue3, total_revenue4,
                total_expenses3, total_expenses4,
                total_ministries3, total_ministries4,
                total_da3, total_da4
        );
    }
    
    public void comparisons_of_two_years(int year1, int year2) {
        try {
            // Use the new compareYears method to get data
            ComparisonResults results = compareYears(year1, year2);
            
            // Print summary section
            System.out.println("================================ SUMMARY ====================================================");
            System.out.printf(
                    "%-35s | %12s | %12s | %12s | %8s%n",
                    "ΧΡΟΝΙΑ",
                    year1,
                    year2,
                    "Διαφορά",
                    "%"
            );
            System.out.println("=============================================================================================");

            printComparison("Budget result", results.getBudgetResult1(), results.getBudgetResult2(), year1, year2);
            printComparison("Total revenue", results.getTotalRevenueSummary1(), results.getTotalRevenueSummary2(), year1, year2);
            printComparison("Total expenses", results.getTotalExpensesSummary1(), results.getTotalExpensesSummary2(), year1, year2);
            printComparison("Total ministries", results.getTotalMinistriesSummary1(), results.getTotalMinistriesSummary2(), year1, year2);
            printComparison("Total decentralized administrations", results.getTotalDASummary1(), results.getTotalDASummary2(), year1, year2);

            // Print revenues
            System.out.println();
            System.out.println("================================ REVENUE =====================================================");
            System.out.printf(
                    "%-35s | %12s | %12s | %12s | %8s%n",
                    "ΧΡΟΝΙΑ",
                    year1,
                    year2,
                    "Διαφορά",
                    "%"
            );
            System.out.println("=============================================================================================");

            for (ComparisonData compData : results.getRevenues().values()) {
                printComparison(compData.getCategoryName(), compData.getYear1Value(), compData.getYear2Value(), year1, year2);
            }

            // Print expenses
            System.out.println();
            System.out.println("================================ EXPENSES ===================================================");
            System.out.printf(
                    "%-35s | %12s | %12s | %12s | %8s%n",
                    "ΧΡΟΝΙΑ",
                    year1,
                    year2,
                    "Διαφορά",
                    "%"
            );
            System.out.println("=============================================================================================");

            for (ComparisonData compData : results.getExpenses().values()) {
                printComparison(compData.getCategoryName(), compData.getYear1Value(), compData.getYear2Value(), year1, year2);
            }

            // Print decentralized administrations
            System.out.println();
            System.out.println("=========================== DECENTRALIZED ADMINISTRATIONS ===================================");
            System.out.printf(
                    "%-35s | %12s | %12s | %12s | %8s%n",
                    "ΧΡΟΝΙΑ",
                    year1,
                    year2,
                    "Διαφορά",
                    "%"
            );
            System.out.println("=============================================================================================");

            for (ComparisonData compData : results.getAdministrations().values()) {
                printComparison(compData.getCategoryName(), compData.getYear1Value(), compData.getYear2Value(), year1, year2);
            }

            // Print ministries
            System.out.println();
            System.out.println("================================ MINISTRIES ================================================");
            System.out.printf(
                    "%-35s | %12s | %12s | %12s | %8s%n",
                    "ΧΡΟΝΙΑ",
                    year1,
                    year2,
                    "Διαφορά",
                    "%"
            );
            System.out.println("=============================================================================================");

            for (ComparisonData compData : results.getMinistries().values()) {
                printComparison(compData.getCategoryName(), compData.getYear1Value(), compData.getYear2Value(), year1, year2);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void printComparison(
        String name,
        long value1,
        long value2,
        int year1,
        int year2
) {
    long diff = value2 - value1;

    String percent;
    if (value1 == 0) {
        percent = "N/A";
    } else {
        percent = String.format("%.2f%%", (double) diff / value1 * 100);
    }

    System.out.printf(
        "%-35s | %12d | %12d | %12d | %8s%n",
        name,
        value1,
        value2,
        diff,
        percent
    );
}

}
