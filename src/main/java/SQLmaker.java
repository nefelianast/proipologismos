import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLmaker {
     public void make() {
        String DBurl = "jdbc:sqlite:src/main/resources/database/BudgetData.db";
       

        try {
            Connection conn = DriverManager.getConnection(DBurl);
            String sql1 = "CREATE TABLE IF NOT EXISTS revenue_2025 ("
        +"total_revenue MONEY PRIMARY KEY,"
        +"taxes MONEY,"
        +"social_contributions MONEY,"
        +"transfers MONEY,"
        +"sales_of_goods_and_services MONEY,"
        +"other_current_revenue MONEY,"
        +"fixed_assets MONEY,"
        +"debt_securities MONEY,"
        +"loans MONEY,"
        +"equity_securities_and_fund_shares MONEY,"
        +"currency_and_deposit_liabilities MONEY,"
        +"debt_securities_liabilities MONEY,"
        +"loans_liabilities MONEY,"
        +"financial_derivatives MONEY"
        +");";
        String sql2 = "CREATE TABLE IF NOT EXISTS expenses_2025 (" 
        +"    total_expenses MONEY PRIMARY KEY," 
        +"    employee_benefits MONEY," 
        +"    social_benefits MONEY," 
        +"    transfers MONEY," 
        +"    purchases_of_goods_and_services MONEY," 
        +"    subsidies MONEY," 
        +"    interest MONEY," 
        +"    other_expenditures MONEY," 
        +"    appropriations MONEY," 
        +"    fixed_assets MONEY,"  
        +"    valuables MONEY," 
        +"    loans MONEY,"  
        +"    equity_securities_and_fund_shares MONEY," 
        +"    debt_securities_liabilities MONEY," 
        +"    loans_liabilities MONEY"  
        +");";
        String sql3 =
        "CREATE TABLE IF NOT EXISTS ministries_2025 ("
        +"total_ministries MONEY,"  
        +"presidency_of_the_republic MONEY,"
        +"hellenic_parliament MONEY,"
        +"presidency_of_the_government MONEY,"
        +"ministry_of_interior MONEY,"
        +"ministry_of_foreign_affairs MONEY,"
        +"ministry_of_national_defence MONEY,"
        +"ministry_of_health MONEY,"
    +"ministry_of_justice MONEY,"
    +"ministry_of_education_religious_affairs_and_sports MONEY,"
    +"ministry_of_culture MONEY,"
    +"ministry_of_national_economy_and_finance MONEY,"
    +"ministry_of_agricultural_development_and_food MONEY,"
    +"ministry_of_environment_and_energy MONEY,"
    +"ministry_of_labor_and_social_security MONEY,"
    +"ministry_of_social_cohesion_and_family MONEY,"
    +"ministry_of_development MONEY,"
    +"ministry_of_infrastructure_and_transport MONEY,"
    +"ministry_of_maritime_affairs_and_insular_policy MONEY,"
    +"ministry_of_tourism MONEY,"
    +"ministry_of_digital_governance MONEY,"
    +"ministry_of_migration_and_asylum MONEY,"
    +"ministry_of_citizen_protection MONEY,"
    +"ministry_of_climate_crisis_and_civil_protection MONEY,"
    +"PRIMARY KEY (total_ministries)"
    +");";
    String sql4 = "CREATE TABLE IF NOT EXISTS budget_summary_2025 ("
    + "budget_result MONEY primary key,"
    + "total_revenue MONEY,"
    + "total_expenses MONEY,"
    + "total_ministries MONEY,"
    + "total_da MONEY,"
    + "FOREIGN KEY (total_revenue) REFERENCES revenue_2025(total_revenue),"
    + "FOREIGN KEY (total_expenses) REFERENCES expenses_2025(total_expenses),"
    + "FOREIGN KEY (total_ministries) REFERENCES ministries_2025(total_ministries),"
    + "FOREIGN KEY (total_da) REFERENCES decentralized_administrations_2025(total_da)"
    + ");";

    String sql5 = "CREATE TABLE IF NOT EXISTS decentralized_administrations_2025 ("
    +"total_da MONEY,"
    +"decentralized_administration_of_attica MONEY,"
    +"decentralized_administration_of_thessaly_central_greece MONEY,"
    +"decentralized_administration_of_epirus_western_macedonia MONEY,"
    +"decentralized_administration_of_peloponnese_western_greece_and_ionian MONEY,"
    +"decentralized_administration_of_aegean MONEY,"
    +"decentralized_administration_of_crete MONEY,"
    +"decentralized_administration_of_macedonia_thrace MONEY,"
    +"PRIMARY KEY (total_da)"
    + ");";
    String sql6 = "CREATE TABLE IF NOT EXISTS revenue_2024 ("
    +"total_revenue MONEY PRIMARY KEY,"
    +"taxes MONEY,"
    +"social_contributions MONEY,"
    +"transfers MONEY,"
    +"sales_of_goods_and_services MONEY,"
    +"other_current_revenue MONEY,"
    +"fixed_assets MONEY,"
    +"debt_securities MONEY,"
    +"equity_securities_and_fund_shares MONEY,"
    +"currency_and_deposit_liabilities MONEY,"
    +"debt_securities_liabilities MONEY,"
    +"loans_liabilities MONEY,"
    +"financial_derivatives MONEY"
    +");";
    String sql7 = "CREATE TABLE IF NOT EXISTS expenses_2024(" 
    +"    total_expenses MONEY PRIMARY KEY," 
    +"    employee_benefits MONEY," 
    +"    social_benefits MONEY," 
    +"    transfers MONEY," 
    +"    purchases_of_goods_and_services MONEY," 
    +"    subsidies MONEY," 
    +"    interest MONEY," 
    +"    other_expenditures MONEY," 
    +"    appropriations MONEY," 
    +"    fixed_assets MONEY,"  
    +"    valuables MONEY," 
     +"    loans MONEY," 
    +"    equity_securities_and_fund_shares MONEY," 
    +"    debt_securities_liabilities MONEY," 
    +"    loans_liabilities MONEY"  
    +     ");";
        String sql8 =  "CREATE TABLE IF NOT EXISTS ministries_2024 ("
    +"total_ministries MONEY,"  
    +"presidency_of_the_republic MONEY,"
    +"hellenic_parliament MONEY,"
    +"presidency_of_the_government MONEY,"
    +"ministry_of_interior MONEY,"
    +"ministry_of_foreign_affairs MONEY,"
    +"ministry_of_national_defence MONEY,"
   +"ministry_of_health MONEY,"
    +"ministry_of_justice MONEY,"
    +"ministry_of_education_religious_affairs_and_sports MONEY,"
    +"ministry_of_culture MONEY,"
    +"ministry_of_national_economy_and_finance MONEY,"
    +"ministry_of_agricultural_development_and_food MONEY,"
    +"ministry_of_environment_and_energy MONEY,"
    +"ministry_of_labor_and_social_security MONEY,"
    +"ministry_of_social_cohesion_and_family MONEY,"
    +"ministry_of_development MONEY,"
    +"ministry_of_infrastructure_and_transport MONEY,"
    +"ministry_of_maritime_affairs_and_insular_policy MONEY,"
    +"ministry_of_tourism MONEY,"
    +"ministry_of_digital_governance MONEY,"
    +"ministry_of_migration_and_asylum MONEY,"
    +"ministry_of_citizen_protection MONEY,"
    +"ministry_of_climate_crisis_and_civil_protection MONEY,"
    +"PRIMARY KEY (total_ministries)"
    +");";
        String sql9 = "CREATE TABLE IF NOT EXISTS budget_summary_2024 ("
    + "budget_result MONEY primary key,"
    + "total_revenue MONEY,"
    + "total_expenses MONEY,"
    + "total_ministries MONEY,"
    + "total_da MONEY,"
    + "FOREIGN KEY (total_revenue) REFERENCES revenue_2024(total_revenue),"
    + "FOREIGN KEY (total_expenses) REFERENCES expenses_2024(total_expenses),"
    + "FOREIGN KEY (total_ministries) REFERENCES ministries_2024(total_ministries),"
    + "FOREIGN KEY (total_da) REFERENCES decentralized_administrations_2024(total_da)"
    + ");";
        String sql10 = "CREATE TABLE IF NOT EXISTS decentralized_administrations_2024 ("
    +"total_da MONEY,"
    +"decentralized_administration_of_attica MONEY,"
    +"decentralized_administration_of_thessaly_central_greece MONEY,"
    +"decentralized_administration_of_epirus_western_macedonia MONEY,"
    +"decentralized_administration_of_peloponnese_western_greece_and_ionian MONEY,"
    +"decentralized_administration_of_aegean MONEY,"
    +"decentralized_administration_of_crete MONEY,"
    +"decentralized_administration_of_macedonia_thrace MONEY,"
    +"PRIMARY KEY (total_da)"
    + ");";
            String sql11 = "CREATE TABLE IF NOT EXISTS revenue_2023 ("
        +"total_revenue MONEY PRIMARY KEY,"
        +"taxes MONEY,"
        +"social_contributions MONEY,"
        +"transfers MONEY,"
        +"sales_of_goods_and_services MONEY,"
        +"other_current_revenue MONEY,"
        +"fixed_assets MONEY,"
        +"debt_securities MONEY,"
        +"equity_securities_and_fund_shares MONEY,"
        +"currency_and_deposit_liabilities MONEY,"
        +"debt_securities_liabilities MONEY,"
        +"loans_liabilities MONEY,"
        +"financial_derivatives MONEY"
        +");";
          String sql12 = "CREATE TABLE IF NOT EXISTS expenses_2023 (" 
        +"    total_expenses MONEY PRIMARY KEY," 
        +"    employee_benefits MONEY," 
        +"    social_benefits MONEY," 
        +"    transfers MONEY," 
        +"    purchases_of_goods_and_services MONEY," 
        +"    subsidies MONEY," 
        +"    interest MONEY," 
        +"    other_expenditures MONEY," 
        +"    appropriations MONEY," 
        +"    fixed_assets MONEY,"  
        +"    valuables MONEY," 
        +"    loans MONEY,"  
        +"    equity_securities_and_fund_shares MONEY," 
        +"    debt_securities_liabilities MONEY," 
        +"    loans_liabilities MONEY"  
        +");";  
        String sql13 = " CREATE TABLE IF NOT EXISTS ministries_2023 ("
        +"total_ministries MONEY,"  
    +"presidency_of_the_republic MONEY,"
    +"hellenic_parliament MONEY,"
    +"presidency_of_the_government MONEY,"
    +"ministry_of_interior MONEY,"
    +"ministry_of_foreign_affairs MONEY,"
    +"ministry_of_national_defence MONEY,"
    +"ministry_of_health MONEY,"
    +"ministry_of_justice MONEY,"
    +"ministry_of_education_religious_affairs_and_sports MONEY,"
   + "ministry_of_culture MONEY,"
    +"ministry_of_national_economy_and_finance MONEY,"
    +"ministry_of_agricultural_development_and_food MONEY,"
    +"ministry_of_environment_and_energy MONEY,"
    +"ministry_of_labor_and_social_security MONEY,"
    +"ministry_of_development MONEY,"
    +"ministry_of_infrastructure_and_transport MONEY,"
   + "ministry_of_maritime_affairs_and_insular_policy MONEY,"
    +"ministry_of_tourism MONEY,"
    +"ministry_of_digital_governance MONEY,"
    +"ministry_of_migration_and_asylum MONEY,"
    +"ministry_of_citizen_protection MONEY,"
    +"ministry_of_climate_crisis_and_civil_protection MONEY,"
    +"PRIMARY KEY (total_ministries)"
    +");";
    String sql14 = "CREATE TABLE IF NOT EXISTS decentralized_administrations_2023 ("
    +"total_da MONEY,"
    +"decentralized_administration_of_attica MONEY,"
    +"decentralized_administration_of_thessaly_central_greece MONEY,"
    +"decentralized_administration_of_epirus_western_macedonia MONEY,"
    +"decentralized_administration_of_peloponnese_western_greece_and_ionian MONEY,"
    +"decentralized_administration_of_aegean MONEY,"
    +"decentralized_administration_of_crete MONEY,"
    +"decentralized_administration_of_macedonia_thrace MONEY,"
    +"PRIMARY KEY (total_da)"
    + ");";
      String sql15 = "CREATE TABLE IF NOT EXISTS budget_summary_2023 ("
    + "budget_result MONEY primary key,"
    + "total_revenue MONEY,"
    + "total_expenses MONEY,"
    + "total_ministries MONEY,"
    + "total_da MONEY,"
    + "FOREIGN KEY (total_revenue) REFERENCES revenue_2023(total_revenue),"
    + "FOREIGN KEY (total_expenses) REFERENCES expenses_2023(total_expenses),"
    + "FOREIGN KEY (total_ministries) REFERENCES ministries_2023(total_ministries),"
    + "FOREIGN KEY (total_da) REFERENCES decentralized_administrations_2023(total_da)"
    + ");";
    String sql16 ="CREATE TABLE IF NOT EXISTS revenue_2026 ("
        +"total_revenue MONEY PRIMARY KEY,"
        +"taxes MONEY,"
        +"social_contributions MONEY,"
        +"transfers MONEY,"
        +"sales_of_goods_and_services MONEY,"
        +"other_current_revenue MONEY,"
        +"fixed_assets MONEY,"
        +"debt_securities MONEY,"
        +"loans MONEY,"
        +"equity_securities_and_fund_shares MONEY,"
        +"currency_and_deposit_liabilities MONEY,"
        +"debt_securities_liabilities MONEY,"
        +"loans_liabilities MONEY,"
        +"financial_derivatives MONEY"
        +");";
    String sql17 ="CREATE TABLE IF NOT EXISTS expenses_2026 (" 
        +"    total_expenses MONEY PRIMARY KEY," 
        +"    employee_benefits MONEY," 
        +"    social_benefits MONEY," 
        +"    transfers MONEY," 
        +"    purchases_of_goods_and_services MONEY," 
        +"    subsidies MONEY," 
        +"    interest MONEY," 
        +"    other_expenditures MONEY," 
        +"    appropriations MONEY," 
        +"    fixed_assets MONEY,"  
        +"    valuables MONEY," 
        +"    loans MONEY,"  
        +"    equity_securities_and_fund_shares MONEY," 
        +"    debt_securities_liabilities MONEY," 
        +"    loans_liabilities MONEY,"  
        +"    financial_derivatives MONEY"  
        +");";
    String sql18 ="CREATE TABLE IF NOT EXISTS ministries_2026 ("
        +"total_ministries MONEY,"  
        +"presidency_of_the_republic MONEY,"
        +"hellenic_parliament MONEY,"
        +"presidency_of_the_government MONEY,"
        +"ministry_of_interior MONEY,"
        +"ministry_of_foreign_affairs MONEY,"
        +"ministry_of_national_defence MONEY,"
        +"ministry_of_health MONEY,"
    +"ministry_of_justice MONEY,"
    +"ministry_of_education_religious_affairs_and_sports MONEY,"
    +"ministry_of_culture MONEY,"
    +"ministry_of_national_economy_and_finance MONEY,"
    +"ministry_of_agricultural_development_and_food MONEY,"
    +"ministry_of_environment_and_energy MONEY,"
    +"ministry_of_labor_and_social_security MONEY,"
    +"ministry_of_social_cohesion_and_family MONEY,"
    +"ministry_of_development MONEY,"
    +"ministry_of_infrastructure_and_transport MONEY,"
    +"ministry_of_maritime_affairs_and_insular_policy MONEY,"
    +"ministry_of_tourism MONEY,"
    +"ministry_of_digital_governance MONEY,"
    +"ministry_of_migration_and_asylum MONEY,"
    +"ministry_of_citizen_protection MONEY,"
    +"ministry_of_climate_crisis_and_civil_protection MONEY,"
    +"PRIMARY KEY (total_ministries)"
    +");";
    String sql19 ="CREATE TABLE IF NOT EXISTS decentralized_administrations_2026 ("
    +"total_da MONEY,"
    +"decentralized_administration_of_attica MONEY,"
    +"decentralized_administration_of_thessaly_central_greece MONEY,"
    +"decentralized_administration_of_epirus_western_macedonia MONEY,"
    +"decentralized_administration_of_peloponnese_western_greece_and_ionian MONEY,"
    +"decentralized_administration_of_aegean MONEY,"
    +"decentralized_administration_of_crete MONEY,"
    +"decentralized_administration_of_macedonia_thrace MONEY,"
    +"PRIMARY KEY (total_da)"
    + ");";
    String sql20 ="CREATE TABLE IF NOT EXISTS budget_summary_2026 ("
    + "budget_result MONEY primary key,"
    + "total_revenue MONEY,"
    + "total_expenses MONEY,"
    + "total_ministries MONEY,"
    + "total_da MONEY,"
    + "FOREIGN KEY (total_revenue) REFERENCES revenue_2025(total_revenue),"
    + "FOREIGN KEY (total_expenses) REFERENCES expenses_2025(total_expenses),"
    + "FOREIGN KEY (total_ministries) REFERENCES ministries_2025(total_ministries),"
    + "FOREIGN KEY (total_da) REFERENCES decentralized_administrations_2025(total_da)"
    + ");";
    
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql1);
            stmt.execute(sql2);
            stmt.execute(sql3);
            stmt.execute(sql4);
            stmt.execute(sql5);
            stmt.execute(sql6);
            stmt.execute(sql7);
            stmt.execute(sql8);
            stmt.execute(sql9);
            stmt.execute(sql10);
            stmt.execute(sql11);
            stmt.execute(sql12);
            stmt.execute(sql13);
            stmt.execute(sql14);
            stmt.execute(sql15);
            stmt.execute(sql16);
            stmt.execute(sql17);
            stmt.execute(sql18);
            stmt.execute(sql19);
            stmt.execute(sql20);

            System.out.println("Table created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        
        SQLinserter test = new SQLinserter();
        try {
            test.insertRevenue2025();
             
            test.insertExpenses2025();
           
           test.insertMinistries2025();
           
           test.insertDecentralizedAdministrations2025();
           
          test.insertBudgetSummary2025();
          
         test.insertRevenue2024();
          
         test.insertExpenses2024();
         
        test.insertMinistries2024();
        
        test.insertDecentralizedAdministrations2024();
         
        test.insertBudgetSummary2024();
        
       test.insertRevenue2023();
       
      test.insertExpenses2023();
      
     test.insertMinistries2023();
     
    test.insertDecentralizedAdministrations2023();
   
   test.insertBudgetSummary2023();
   test.insertRevenue2026();
   test.insertExpenses2026();
   test.insertMinistries2026();
   test.insertDecentralizedAdministrations2026();
   
   test.insertBudgetSummary2026();
   /* 
   String sc="social_contributions";
   test.updateRevenue(2025, 3, sc, "10");*/
 
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
