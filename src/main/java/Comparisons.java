import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Comparisons {
    public void comparisons_of_two_years(int year1,int year2) {
       String DB = "jdbc:sqlite:src/main/resources/database/BudgetData.db";

       try {
            Connection connection = DriverManager.getConnection(DB);



        Statement stmt = connection.createStatement();
        String sql = "SELECT * FROM revenue_"+year1;
        ResultSet rs = stmt.executeQuery(sql);
//---------------------revenue--------------------------------------------
        long total_revenue1 = 0;
        long taxes1 = 0;
        long social_contributions1 = 0;
        long transfers1 = 0;
        long sales_of_goods_and_services1 = 0;
        long other_current_revenue1 = 0;
        long fixed_assets1 = 0;
        long debt_securities1 = 0;
        long loans1 = 0;
        long equity_securities_and_fund_shares1 = 0;
        long currency_and_deposit_liabilities1 = 0;
        long debt_securities_liabilities1 = 0;
        long loans_liabilities1 = 0;
        long financial_derivatives1 = 0;

        long total_revenue2 = 0;
        long taxes2 = 0;
        long social_contributions2 = 0;
        long transfers2 = 0;
        long sales_of_goods_and_services2 = 0;
        long other_current_revenue2 = 0;
        long fixed_assets2 = 0;
        long debt_securities2 = 0;
        long loans2 = 0;
        long equity_securities_and_fund_shares2 = 0;
        long currency_and_deposit_liabilities2 = 0;
        long debt_securities_liabilities2 = 0;
        long loans_liabilities2 = 0;
        long financial_derivatives2 = 0;
//---------------------expense---------------------------------------------

        long total_expenses = 0;
        long employee_benefits = 0;
        long social_benefits = 0;
        long transfersexp1 = 0;
        long purchases_of_goods_and_services = 0;
        long subsidies = 0;
        long interest  = 0;
        long other_expenditures = 0;
        long appropriations = 0;
        long fixed_assets = 0;
        long valuables = 0;
        long loansexp1 = 0;
        long equity_securities_and_fund_shares = 0;
        long debt_securities_liabilities = 0;
        long loans_liabilities = 0;


        long total_expenses2 = 0;
        long employee_benefits2 = 0;
        long social_benefits2 = 0;
        long transfersexp2 = 0;
        long purchases_of_goods_and_services2 = 0;
        long subsidies2 = 0;
        long interest2  = 0;
        long other_expenditures2 = 0;
        long appropriations2 = 0;
        long fixed_assets22 = 0;
        long valuables2 = 0;
        long loansexp2= 0;
        long equity_securities_and_fund_shares22 = 0;
        long debt_securities_liabilities22= 0;
        long loans_liabilities22 = 0;

//-------------------------DE_AD----------------------------------------

        long total_da = 0;
        long decentralized_administration_of_attica = 0;
        long decentralized_administration_of_thessaly_central_greece = 0;
        long decentralized_administration_of_epirus_western_macedonia = 0;
        long decentralized_administration_of_peloponnese_western_greece_and_ionian = 0;
        long decentralized_administration_of_aegean = 0;
        long decentralized_administration_of_crete = 0;
        long decentralized_administration_of_macedonia_thrace = 0;


        long total_da2 = 0;
        long decentralized_administration_of_attica2 = 0;
        long decentralized_administration_of_thessaly_central_greece2 = 0;
        long decentralized_administration_of_epirus_western_macedonia2 = 0;
        long decentralized_administration_of_peloponnese_western_greece_and_ionian2 = 0;
        long decentralized_administration_of_aegean2 = 0;
        long decentralized_administration_of_crete2 = 0;
        long decentralized_administration_of_macedonia_thrace2 = 0;

//---------------------ministries-----------------------------------------

        long total_ministries = 0;
        long presidency_of_the_republic = 0;
        long hellenic_parliament = 0;
        long presidency_of_the_government = 0;
        long ministry_of_interior = 0;
        long ministry_of_foreign_affairs = 0;
        long ministry_of_national_defence  = 0;
        long ministry_of_health = 0;
        long ministry_of_justice = 0;
        long ministry_of_education_religious_affairs_and_sports = 0;
        long ministry_of_culture = 0;
        long ministry_of_national_economy_and_finance = 0;
        long ministry_of_agricultural_development_and_food = 0;
        long ministry_of_environment_and_energy = 0;
        long ministry_of_labor_and_social_security = 0;
        long ministry_of_social_cohesion_and_family = 0;
        long ministry_of_development = 0;
        long ministry_of_infrastructure_and_transport = 0;
        long ministry_of_maritime_affairs_and_insular_policy = 0;
        long ministry_of_tourism = 0;
        long ministry_of_digital_governance = 0;
        long ministry_of_migration_and_asylum = 0;
        long ministry_of_citizen_protection = 0;
        long ministry_of_climate_crisis_and_civil_protection = 0;


        long total_ministries2 = 0;
        long presidency_of_the_republic2 = 0;
        long hellenic_parliament2 = 0;
        long presidency_of_the_government2 = 0;
        long ministry_of_interior2 = 0;
        long ministry_of_foreign_affairs2 = 0;
        long ministry_of_national_defence2  = 0;
        long ministry_of_health2 = 0;
        long ministry_of_justice2 = 0;
        long ministry_of_education_religious_affairs_and_sports2 = 0;
        long ministry_of_culture2 = 0;
        long ministry_of_national_economy_and_finance2 = 0;
        long ministry_of_agricultural_development_and_food2 = 0;
        long ministry_of_environment_and_energy2 = 0;
        long ministry_of_labor_and_social_security2 = 0;
        long ministry_of_social_cohesion_and_family2 = 0;
        long ministry_of_development2 = 0;
        long ministry_of_infrastructure_and_transport2 = 0;
        long ministry_of_maritime_affairs_and_insular_policy2 = 0;
        long ministry_of_tourism2 = 0;
        long ministry_of_digital_governance2 = 0;
        long ministry_of_migration_and_asylum2 = 0;
        long ministry_of_citizen_protection2 = 0;
        long ministry_of_climate_crisis_and_civil_protection2 = 0;
//----------------------Summary------------------------------------

        long budget_result = 0;
        long total_revenue3 = 0;
        long total_expenses3 = 0;
        long total_ministries3 = 0;
        long total_da3 = 0;


        long budget_result2 = 0;
        long total_revenue4 = 0;
        long total_expenses4 = 0;
        long total_ministries4 = 0;
        long total_da4 = 0;



//-------------------------Code----------------------------------------       
        while (rs.next()) {
            total_revenue1 = rs.getLong("total_revenue");
            taxes1 = rs.getLong("taxes");
            social_contributions1 = rs.getLong("social_contributions");
            transfers1 = rs.getLong("transfers");
            sales_of_goods_and_services1 = rs.getLong("sales_of_goods_and_services");
            other_current_revenue1 = rs.getLong("other_current_revenue");

            fixed_assets1 = rs.getLong("fixed_assets");
            debt_securities1 = rs.getLong("debt_securities");
            if (year1==2025 || year1==2026)
            {loans1 = rs.getLong("loans");}
            
           
            
            equity_securities_and_fund_shares1 = rs.getLong("equity_securities_and_fund_shares");

            currency_and_deposit_liabilities1 = rs.getLong("currency_and_deposit_liabilities");
            debt_securities_liabilities1 = rs.getLong("debt_securities_liabilities");
            loans_liabilities1 = rs.getLong("loans_liabilities");
            financial_derivatives1 = rs.getLong("financial_derivatives");

        
            

            System.out.println("total_revenue: " + total_revenue1);
            System.out.println("taxes: " + taxes1);
            System.out.println("social_contributions: " + social_contributions1);
            System.out.println("transfers: " + transfers1);
            System.out.println("sales_of_goods_and_services: " + sales_of_goods_and_services1);
            System.out.println("other_current_revenue: " + other_current_revenue1);
            System.out.println("fixed_assets: " + fixed_assets1);
            System.out.println("debt_securities: " + debt_securities1);
            System.out.println("loans: " + loans1);
            System.out.println("equity_securities_and_fund_shares: " + equity_securities_and_fund_shares1);
            System.out.println("currency_and_deposit_liabilities: " + currency_and_deposit_liabilities1);
            System.out.println("debt_securities_liabilities: " + debt_securities_liabilities1);
            System.out.println("loans_liabilities: " + loans_liabilities1);
            System.out.println("financial_derivatives: " + financial_derivatives1);

                    
        }
        String sq2 ="SELECT * FROM revenue_"+year2;
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
            if (year2==2025 || year2==2026)
            {loans2 = rs2.getLong("loans");}
            
            equity_securities_and_fund_shares2 = rs2.getLong("equity_securities_and_fund_shares");

            currency_and_deposit_liabilities2 = rs2.getLong("currency_and_deposit_liabilities");
            debt_securities_liabilities2 = rs2.getLong("debt_securities_liabilities");
            loans_liabilities2 = rs2.getLong("loans_liabilities");
            financial_derivatives2 = rs2.getLong("financial_derivatives");

        
            

            System.out.println("total_revenue: " + total_revenue2);
            System.out.println("taxes: " + taxes2);
            System.out.println("social_contributions: " + social_contributions2);
            System.out.println("transfers: " + transfers2);
            System.out.println("sales_of_goods_and_services: " + sales_of_goods_and_services2);
            System.out.println("other_current_revenue: " + other_current_revenue2);
            System.out.println("fixed_assets: " + fixed_assets2);
            System.out.println("debt_securities: " + debt_securities2);
            System.out.println("loans: " + loans2);
            System.out.println("equity_securities_and_fund_shares: " + equity_securities_and_fund_shares2);
            System.out.println("currency_and_deposit_liabilities: " + currency_and_deposit_liabilities2);
            System.out.println("debt_securities_liabilities: " + debt_securities_liabilities2);
            System.out.println("loans_liabilities: " + loans_liabilities2);
            System.out.println("financial_derivatives: " + financial_derivatives2);

                    
        }
        String sq3 ="SELECT * FROM expenses_"+year1;
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

        
            

            System.out.println("total_expenses: " + total_expenses);
            System.out.println("employee_benefits: " + employee_benefits);
            System.out.println("social_benefits: " + social_benefits);
            System.out.println("transfers: " + transfersexp1);
            System.out.println("purchases_of_goods_and_services: " + purchases_of_goods_and_services);
            System.out.println("subsidies: " + subsidies);
            System.out.println("interest: " + interest);
            System.out.println("other_expenditures: " + other_expenditures);
            System.out.println("loans: " + loansexp1);
            System.out.println("appropriations: " + appropriations);
            System.out.println("fixed_assets: " + fixed_assets);
            System.out.println("valuables: " + valuables);
            System.out.println("equity_securities_and_fund_shares: " + equity_securities_and_fund_shares);
            System.out.println("debt_securities_liabilities: " + debt_securities_liabilities);
            System.out.println("loans_liabilities: " + loans_liabilities);

                    
        }
        String sq4 ="SELECT * FROM expenses_"+year2;
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

        
            

            System.out.println("total_expenses: " + total_expenses2);
            System.out.println("employee_benefits: " + employee_benefits2);
            System.out.println("social_benefits: " + social_benefits2);
            System.out.println("transfers: " + transfersexp2);
            System.out.println("purchases_of_goods_and_services: " + purchases_of_goods_and_services2);
            System.out.println("subsidies: " + subsidies2);
            System.out.println("interest: " + interest2);
            System.out.println("other_expenditures: " + other_expenditures2);
            System.out.println("loans: " + loansexp2);
            System.out.println("appropriations: " + appropriations2);
            System.out.println("fixed_assets: " + fixed_assets22);
            System.out.println("valuables: " + valuables2);
            System.out.println("equity_securities_and_fund_shares: " + equity_securities_and_fund_shares22);
            System.out.println("debt_securities_liabilities: " + debt_securities_liabilities22);
            System.out.println("loans_liabilities: " + loans_liabilities22);

                    
        }

        String sq5 ="SELECT * FROM decentralized_administrations_"+year1;
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
            
        
            

            System.out.println("total_decentralized_administrations: " + total_da);
            System.out.println("decentralized_administration_of_attica: " + decentralized_administration_of_attica);
            System.out.println("decentralized_administration_of_thessaly_central_greece: " + decentralized_administration_of_thessaly_central_greece);
            System.out.println("decentralized_administration_of_epirus_western_macedonia: " + decentralized_administration_of_epirus_western_macedonia);
            System.out.println("decentralized_administration_of_peloponnese_western_greece_and_ionian: " + decentralized_administration_of_peloponnese_western_greece_and_ionian);
            System.out.println("decentralized_administration_of_aegean: " + decentralized_administration_of_aegean);
            System.out.println("decentralized_administration_of_crete: " + decentralized_administration_of_crete);
            System.out.println("decentralized_administration_of_macedonia_thrace: " + decentralized_administration_of_macedonia_thrace);
            
           
                    
        }

        String sq6 ="SELECT * FROM decentralized_administrations_"+year2;
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
            
        
            

            System.out.println("total_decentralized_administrations: " + total_da2);
            System.out.println("decentralized_administration_of_attica: " + decentralized_administration_of_attica2);
            System.out.println("decentralized_administration_of_thessaly_central_greece: " + decentralized_administration_of_thessaly_central_greece2);
            System.out.println("decentralized_administration_of_epirus_western_macedonia: " + decentralized_administration_of_epirus_western_macedonia2);
            System.out.println("decentralized_administration_of_peloponnese_western_greece_and_ionian: " + decentralized_administration_of_peloponnese_western_greece_and_ionian2);
            System.out.println("decentralized_administration_of_aegean: " + decentralized_administration_of_aegean2);
            System.out.println("decentralized_administration_of_crete: " + decentralized_administration_of_crete2);
            System.out.println("decentralized_administration_of_macedonia_thrace: " + decentralized_administration_of_macedonia_thrace2);
            
           
                    
        }

        String sq7 ="SELECT * FROM ministries_"+year1;
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
            if (year1==2023){ministry_of_social_cohesion_and_family =0;
            } else { ministry_of_social_cohesion_and_family = rs7.getLong("ministry_of_social_cohesion_and_family");}
            ministry_of_development = rs7.getLong("ministry_of_development");
            ministry_of_infrastructure_and_transport = rs7.getLong("ministry_of_infrastructure_and_transport");
            ministry_of_maritime_affairs_and_insular_policy = rs7.getLong("ministry_of_maritime_affairs_and_insular_policy");
            ministry_of_tourism = rs7.getLong("ministry_of_tourism");
            ministry_of_digital_governance = rs7.getLong("ministry_of_digital_governance");
            ministry_of_migration_and_asylum = rs7.getLong("ministry_of_migration_and_asylum");
            ministry_of_citizen_protection = rs7.getLong("ministry_of_citizen_protection");
            ministry_of_climate_crisis_and_civil_protection = rs7.getLong("ministry_of_climate_crisis_and_civil_protection");
            
            

            System.out.println("total_ministries: " + total_ministries);
            System.out.println("presidency_of_the_republic: " + presidency_of_the_republic);
            System.out.println("hellenic_parliament: " +  hellenic_parliament);
            System.out.println("presidency_of_the_government: " + presidency_of_the_government);
            System.out.println("ministry_of_interior: " + ministry_of_interior);
            System.out.println("ministry_of_foreign_affairs: " + ministry_of_foreign_affairs);
            System.out.println("ministry_of_national_defence: " + ministry_of_national_defence);
            System.out.println("ministry_of_health: " + ministry_of_health);
            System.out.println("ministry_of_justice: " + ministry_of_justice);
            System.out.println("ministry_of_education_religious_affairs_and_sports: " + ministry_of_education_religious_affairs_and_sports);
            System.out.println("ministry_of_culture: " + ministry_of_culture);
            System.out.println("ministry_of_national_economy_and_finance: " + ministry_of_national_economy_and_finance);
            System.out.println("ministry_of_agricultural_development_and_food: " + ministry_of_agricultural_development_and_food);
            System.out.println("ministry_of_environment_and_energy: " + ministry_of_environment_and_energy);
            System.out.println("ministry_of_labor_and_social_security: " + ministry_of_labor_and_social_security);
            System.out.println("ministry_of_social_cohesion_and_family: " + ministry_of_social_cohesion_and_family);
            System.out.println("ministry_of_development: " + ministry_of_development);
            System.out.println("ministry_of_infrastructure_and_transport: " + ministry_of_infrastructure_and_transport);
            System.out.println("ministry_of_maritime_affairs_and_insular_policy: " + ministry_of_maritime_affairs_and_insular_policy);
            System.out.println("ministry_of_tourism: " + ministry_of_tourism);
            System.out.println("ministry_of_digital_governance: " + ministry_of_digital_governance);
            System.out.println("ministry_of_migration_and_asylum: " + ministry_of_migration_and_asylum);
            System.out.println("ministry_of_citizen_protection: " + ministry_of_citizen_protection);
            System.out.println("ministry_of_climate_crisis_and_civil_protection: " + ministry_of_climate_crisis_and_civil_protection);
                    
        }

        String sq8 ="SELECT * FROM ministries_"+year2;
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
            if (year2==2023){ministry_of_social_cohesion_and_family2 =0;
            } else { ministry_of_social_cohesion_and_family2 = rs8.getLong("ministry_of_social_cohesion_and_family");}
            ministry_of_development2 = rs8.getLong("ministry_of_development");
            ministry_of_infrastructure_and_transport2 = rs8.getLong("ministry_of_infrastructure_and_transport");
            ministry_of_maritime_affairs_and_insular_policy2 = rs8.getLong("ministry_of_maritime_affairs_and_insular_policy");
            ministry_of_tourism2 = rs8.getLong("ministry_of_tourism");
            ministry_of_digital_governance2 = rs8.getLong("ministry_of_digital_governance");
            ministry_of_migration_and_asylum2 = rs8.getLong("ministry_of_migration_and_asylum");
            ministry_of_citizen_protection2 = rs8.getLong("ministry_of_citizen_protection");
            ministry_of_climate_crisis_and_civil_protection2 = rs8.getLong("ministry_of_climate_crisis_and_civil_protection");
            
            

            System.out.println("total_ministries: " + total_ministries2);
            System.out.println("presidency_of_the_republic: " + presidency_of_the_republic2);
            System.out.println("hellenic_parliament: " +  hellenic_parliament2);
            System.out.println("presidency_of_the_government: " + presidency_of_the_government2);
            System.out.println("ministry_of_interior: " + ministry_of_interior2);
            System.out.println("ministry_of_foreign_affairs: " + ministry_of_foreign_affairs2);
            System.out.println("ministry_of_national_defence: " + ministry_of_national_defence2);
            System.out.println("ministry_of_health: " + ministry_of_health2);
            System.out.println("ministry_of_justice: " + ministry_of_justice2);
            System.out.println("ministry_of_education_religious_affairs_and_sports: " + ministry_of_education_religious_affairs_and_sports2);
            System.out.println("ministry_of_culture: " + ministry_of_culture2);
            System.out.println("ministry_of_national_economy_and_finance: " + ministry_of_national_economy_and_finance2);
            System.out.println("ministry_of_agricultural_development_and_food: " + ministry_of_agricultural_development_and_food2);
            System.out.println("ministry_of_environment_and_energy: " + ministry_of_environment_and_energy2);
            System.out.println("ministry_of_labor_and_social_security: " + ministry_of_labor_and_social_security2);
            System.out.println("ministry_of_social_cohesion_and_family: " + ministry_of_social_cohesion_and_family2);
            System.out.println("ministry_of_development: " + ministry_of_development2);
            System.out.println("ministry_of_infrastructure_and_transport: " + ministry_of_infrastructure_and_transport2);
            System.out.println("ministry_of_maritime_affairs_and_insular_policy: " + ministry_of_maritime_affairs_and_insular_policy2);
            System.out.println("ministry_of_tourism: " + ministry_of_tourism2);
            System.out.println("ministry_of_digital_governance: " + ministry_of_digital_governance2);
            System.out.println("ministry_of_migration_and_asylum: " + ministry_of_migration_and_asylum2);
            System.out.println("ministry_of_citizen_protection: " + ministry_of_citizen_protection2);
            System.out.println("ministry_of_climate_crisis_and_civil_protection: " + ministry_of_climate_crisis_and_civil_protection2);
                    
        }

        String sq9 ="SELECT * FROM budget_summary_"+year1;
        ResultSet rs9 = stmt.executeQuery(sq9);

         while (rs9.next()) {
            budget_result = rs9.getLong("budget_result");
            total_revenue3 = rs9.getLong("total_revenue");
            total_expenses3 = rs9.getLong("total_expenses");
            total_ministries3 = rs9.getLong("total_ministries");
            total_da3 = rs9.getLong("total_da");
            
            
        
            System.out.println("budget_result: " + budget_result);
            System.out.println("total_revenue: " + total_revenue3);
            System.out.println("total_expenses: " + total_expenses3);
            System.out.println("total_ministries: " + total_ministries3);
            System.out.println("total_da: " + total_da3);
        }

        String sq10 ="SELECT * FROM budget_summary_"+year2;
        ResultSet rs10 = stmt.executeQuery(sq10);

         while (rs10.next()) {
            budget_result2 = rs10.getLong("budget_result");
            total_revenue4 = rs10.getLong("total_revenue");
            total_expenses4 = rs10.getLong("total_expenses");
            total_ministries4 = rs10.getLong("total_ministries");
            total_da4 = rs10.getLong("total_da");
            
            
        
            System.out.println("budget_result: " + budget_result2);
            System.out.println("total_revenue: " + total_revenue4);
            System.out.println("total_expenses: " + total_expenses4);
            System.out.println("total_ministries: " + total_ministries4);
            System.out.println("total_da: " + total_da4);
        }



//------------------------------------------------Display------------------------------------------------------------------------------
                        System.out.println();
            System.out.println("================================ BUDGET SUMMARY =============================================");
            System.out.printf(
                    "%-35s | %12s | %12s | %12s | %8s%n",
                    "ΧΡΟΝΙΑ",
                    year1,
                    year2,
                    "Διαφορά",
                    "%"
            );
            System.out.println("=============================================================================================");

            printComparison("Budget result", budget_result, budget_result2, year1, year2);
            printComparison("Total revenue", total_revenue3, total_revenue4, year1, year2);
            printComparison("Total expenses", total_expenses3, total_expenses4, year1, year2);
            printComparison("Total ministries", total_ministries3, total_ministries4, year1, year2);
            printComparison("Total decentralized administrations", total_da3, total_da4, year1, year2);


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



            printComparison("Total revenue", total_revenue1, total_revenue2, year1, year2);
            printComparison("Taxes", taxes1, taxes2, year1, year2);
            printComparison("Social contributions", social_contributions1, social_contributions2, year1, year2);
            printComparison("Transfers", transfers1, transfers2, year1, year2);
            printComparison("Sales of goods & services", sales_of_goods_and_services1, sales_of_goods_and_services2, year1, year2);
            printComparison("Other current revenue", other_current_revenue1, other_current_revenue2, year1, year2);

            printComparison("Fixed assets", fixed_assets1, fixed_assets2, year1, year2);
            printComparison("Debt securities", debt_securities1, debt_securities2, year1, year2);
            if (year1==2025 || year2==2025 || year1==2026 || year2==2026)
            {printComparison("Loans", loans1, loans2, year1, year2);}
            
            printComparison("Equity securities & fund shares", equity_securities_and_fund_shares1, equity_securities_and_fund_shares2, year1, year2);

            printComparison("Currency & deposits liabilities", currency_and_deposit_liabilities1, currency_and_deposit_liabilities2, year1, year2);
            printComparison("Debt securities liabilities", debt_securities_liabilities1, debt_securities_liabilities2, year1, year2);
            printComparison("Loans liabilities", loans_liabilities1, loans_liabilities2, year1, year2);
            printComparison("Financial derivatives", financial_derivatives1, financial_derivatives2, year1, year2);
            

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

            printComparison("Total expenses", total_expenses, total_expenses2, year1, year2);
            printComparison("Employee benefits", employee_benefits, employee_benefits2, year1, year2);
            printComparison("Social benefits", social_benefits, social_benefits2, year1, year2);
            printComparison("Transfers (expenses)", transfersexp1, transfersexp2, year1, year2);
            printComparison("Purchases of goods & services",
                    purchases_of_goods_and_services,
                    purchases_of_goods_and_services2,
                    year1,
                    year2);

            printComparison("Subsidies", subsidies, subsidies2, year1, year2);
            printComparison("Interest", interest, interest2, year1, year2);
            printComparison("Other expenditures", other_expenditures, other_expenditures2, year1, year2);
            printComparison("Loans (expenses)", loansexp1, loansexp2, year1, year2);
            printComparison("Appropriations", appropriations, appropriations2, year1, year2);

            printComparison("Fixed assets (expenses)", fixed_assets, fixed_assets2, year1, year2);
            printComparison("Valuables", valuables, valuables2, year1, year2);
            printComparison("Equity securities & fund shares (exp)",
                    equity_securities_and_fund_shares,
                    equity_securities_and_fund_shares2,
                    year1,
                    year2);

            printComparison("Debt securities liabilities (exp)",
                    debt_securities_liabilities,
                    debt_securities_liabilities2,
                    year1,
                    year2);

            printComparison("Loans liabilities (exp)",
                    loans_liabilities,
                    loans_liabilities2,
                    year1,
                    year2);




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

            printComparison("Total decentralized administrations", total_da, total_da2, year1, year2);

            printComparison("Attica",
                    decentralized_administration_of_attica,
                    decentralized_administration_of_attica2,
                    year1,
                    year2);

            printComparison("Thessaly & Central Greece",
                    decentralized_administration_of_thessaly_central_greece,
                    decentralized_administration_of_thessaly_central_greece2,
                    year1,
                    year2);

            printComparison("Epirus & Western Macedonia",
                    decentralized_administration_of_epirus_western_macedonia,
                    decentralized_administration_of_epirus_western_macedonia2,
                    year1,
                    year2);

            printComparison("Peloponnese, Western Greece & Ionian",
                    decentralized_administration_of_peloponnese_western_greece_and_ionian,
                    decentralized_administration_of_peloponnese_western_greece_and_ionian2,
                    year1,
                    year2);

            printComparison("Aegean",
                    decentralized_administration_of_aegean,
                    decentralized_administration_of_aegean2,
                    year1,
                    year2);

            printComparison("Crete",
                    decentralized_administration_of_crete,
                    decentralized_administration_of_crete2,
                    year1,
                    year2);

            printComparison("Macedonia & Thrace",
                    decentralized_administration_of_macedonia_thrace,
                    decentralized_administration_of_macedonia_thrace2,
                    year1,
                    year2);


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

            printComparison("Total ministries", total_ministries, total_ministries2, year1, year2);

            printComparison("Presidency of the Republic",
                    presidency_of_the_republic,
                    presidency_of_the_republic2,
                    year1,
                    year2);

            printComparison("Hellenic Parliament",
                    hellenic_parliament,
                    hellenic_parliament2,
                    year1,
                    year2);

            printComparison("Presidency of the Government",
                    presidency_of_the_government,
                    presidency_of_the_government2,
                    year1,
                    year2);

            printComparison("Ministry of Interior",
                    ministry_of_interior,
                    ministry_of_interior2,
                    year1,
                    year2);

            printComparison("Ministry of Foreign Affairs",
                    ministry_of_foreign_affairs,
                    ministry_of_foreign_affairs2,
                    year1,
                    year2);

            printComparison("Ministry of National Defence",
                    ministry_of_national_defence,
                    ministry_of_national_defence2,
                    year1,
                    year2);

            printComparison("Ministry of Health",
                    ministry_of_health,
                    ministry_of_health2,
                    year1,
                    year2);

            printComparison("Ministry of Justice",
                    ministry_of_justice,
                    ministry_of_justice2,
                    year1,
                    year2);

            printComparison("Ministry of Education, Religious Affairs & Sports",
                    ministry_of_education_religious_affairs_and_sports,
                    ministry_of_education_religious_affairs_and_sports2,
                    year1,
                    year2);

            printComparison("Ministry of Culture",
                    ministry_of_culture,
                    ministry_of_culture2,
                    year1,
                    year2);

            printComparison("Ministry of National Economy & Finance",
                    ministry_of_national_economy_and_finance,
                    ministry_of_national_economy_and_finance2,
                    year1,
                    year2);

            printComparison("Ministry of Agricultural Development & Food",
                    ministry_of_agricultural_development_and_food,
                    ministry_of_agricultural_development_and_food2,
                    year1,
                    year2);

            printComparison("Ministry of Environment & Energy",
                    ministry_of_environment_and_energy,
                    ministry_of_environment_and_energy2,
                    year1,
                    year2);

            printComparison("Ministry of Labor & Social Security",
                    ministry_of_labor_and_social_security,
                    ministry_of_labor_and_social_security2,
                    year1,
                    year2);

            printComparison("Ministry of Social Cohesion & Family",
                    ministry_of_social_cohesion_and_family,
                    ministry_of_social_cohesion_and_family2,
                    year1,
                    year2);

            printComparison("Ministry of Development",
                    ministry_of_development,
                    ministry_of_development2,
                    year1,
                    year2);

            printComparison("Ministry of Infrastructure & Transport",
                    ministry_of_infrastructure_and_transport,
                    ministry_of_infrastructure_and_transport2,
                    year1,
                    year2);

            printComparison("Ministry of Maritime Affairs & Insular Policy",
                    ministry_of_maritime_affairs_and_insular_policy,
                    ministry_of_maritime_affairs_and_insular_policy2,
                    year1,
                    year2);

            printComparison("Ministry of Tourism",
                    ministry_of_tourism,
                    ministry_of_tourism2,
                    year1,
                    year2);

            printComparison("Ministry of Digital Governance",
                    ministry_of_digital_governance,
                    ministry_of_digital_governance2,
                    year1,
                    year2);

            printComparison("Ministry of Migration & Asylum",
                    ministry_of_migration_and_asylum,
                    ministry_of_migration_and_asylum2,
                    year1,
                    year2);

            printComparison("Ministry of Citizen Protection",
                    ministry_of_citizen_protection,
                    ministry_of_citizen_protection2,
                    year1,
                    year2);

            printComparison("Ministry of Climate Crisis & Civil Protection",
                    ministry_of_climate_crisis_and_civil_protection,
                    ministry_of_climate_crisis_and_civil_protection2,
                    year1,
                    year2);

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
