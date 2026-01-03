import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import ui.DatabaseConnection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

public class SQLinserter {
  
    // ------------------ 2026 ------------------ //

    void insertRevenue2026() throws Exception {
         try {
            Connection connection = DatabaseConnection.getConnection();

            String sql = "INSERT INTO revenue_2026(total_revenue,taxes,social_contributions,transfers,sales_of_goods_and_services,other_current_revenue,fixed_assets,debt_securities,loans,equity_securities_and_fund_shares,currency_and_deposit_liabilities,debt_securities_liabilities,loans_liabilities,financial_derivatives) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2026.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2026.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
             String total_revenue = null;
                String taxes = null;
                String social_contributions = null;
                String transfers = null;
                String sales_of_goods_and_services = null;
                String other_current_revenue = null;
                String fixed_assets = null;
                String debt_securities = null;
                String loans = null;
                String equity_securities_and_fund_shares = null;
                String currency_and_deposit_liabilities = null;
                String debt_securities_liabilities = null;
                String loans_liabilities = null;
                String financial_derivatives = null;
            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");


                if (lineNumber < 14) {
                    continue;
                }
                if (lineNumber > 27) {
                    break;
                }
               
                if (lineNumber == 14) {
                    total_revenue = data[3];
                } else if (lineNumber == 15) {
                    taxes = data[3];
                } else if (lineNumber == 16) {
                    social_contributions = data[3];
                } else if (lineNumber == 17) {
                    transfers = data[3];
                } else if (lineNumber == 18) {
                    sales_of_goods_and_services = data[3];
                } else if (lineNumber == 19) {
                    other_current_revenue = data[3];
                } else if (lineNumber == 20) {
                    fixed_assets = data[3];
                } else if (lineNumber == 21) {
                    debt_securities = data[3];
                } else if (lineNumber == 22) {
                    loans = data[3];
                } else if (lineNumber == 23) {
                    equity_securities_and_fund_shares = data[3];
                } else if (lineNumber == 24) {
                    currency_and_deposit_liabilities = data[3];
                } else if (lineNumber == 25) {
                    debt_securities_liabilities = data[3];
                } else if (lineNumber == 26) {
                    loans_liabilities = data[3];
                } else if (lineNumber == 27) {
                    financial_derivatives = data[3];
                }

            }
             statement.setBigDecimal(1,  parseMoney(total_revenue));
                    statement.setBigDecimal(2,  parseMoney(taxes));
                    statement.setBigDecimal(3,  parseMoney(social_contributions));
                    statement.setBigDecimal(4,  parseMoney(transfers));
                    statement.setBigDecimal(5,  parseMoney(sales_of_goods_and_services));
                    statement.setBigDecimal(6,  parseMoney(other_current_revenue));
                    statement.setBigDecimal(7,  parseMoney(fixed_assets));
                    statement.setBigDecimal(8,  parseMoney(debt_securities));
                    statement.setBigDecimal(9,  parseMoney(loans));
                    statement.setBigDecimal(10, parseMoney(equity_securities_and_fund_shares));
                    statement.setBigDecimal(11, parseMoney(currency_and_deposit_liabilities));
                    statement.setBigDecimal(12, parseMoney(debt_securities_liabilities));
                    statement.setBigDecimal(13, parseMoney(loans_liabilities));
                    statement.setBigDecimal(14, parseMoney(financial_derivatives));

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
      
    }

     void insertExpenses2026() throws Exception {
String sql = "INSERT INTO expenses_2026(total_expenses,employee_benefits,social_benefits,transfers,purchases_of_goods_and_services,subsidies,interest,other_expenditures,appropriations,fixed_assets,valuables,loans,equity_securities_and_fund_shares,debt_securities_liabilities,loans_liabilities,financial_derivatives) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2026.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2026.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
            String total_expenses=null;
            String employee_benefits=null;
            String social_benefits=null;
            String transfers=null;
            String purchases_of_goods_and_services=null;
            String subsidies=null;
            String interest=null;
            String other_expenditures=null;
            String appropriations=null;
            String fixed_assets=null;
            String valuables=null;
            String loans=null;
            String equity_securities_and_fund_shares=null;
            String debt_securities_liabilities=null;
            String loans_liabilities=null;
            String financial_derivatives= null;


            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 28) {
                    continue;
                }
                if (lineNumber > 43) {
                    break;
                }
               
                if (lineNumber == 28) {
                    total_expenses=data[3];
                } else if (lineNumber == 29) {
                    employee_benefits=data[3];
                } else if (lineNumber == 30) {
                    social_benefits=data[3];
                } else if (lineNumber == 31) {
                    transfers=data[3];
                } else if (lineNumber == 32) {
                    purchases_of_goods_and_services=data[3];
                } else if (lineNumber == 33) {
                    subsidies=data[3];
                } else if (lineNumber == 34) {
                    interest=data[3];
                } else if (lineNumber == 35) {
                    other_expenditures=data[3];
                } else if (lineNumber == 36) {
                    appropriations=data[3];
                } else if (lineNumber == 37) {
                    fixed_assets=data[3];
                } else if (lineNumber == 38) {
                    valuables=data[3];
                } else if (lineNumber == 39) {
                    loans=data[3];
                } else if (lineNumber == 40) {
                    equity_securities_and_fund_shares=data[3];
                } else if (lineNumber == 41) {
                    debt_securities_liabilities=data[3];
                } else if (lineNumber == 42) {
                    loans_liabilities=data[3];
                } else if (lineNumber == 43) {
                    financial_derivatives=data[3];
                }
            }
             statement.setBigDecimal(1,  parseMoney(total_expenses));
                    statement.setBigDecimal(2,  parseMoney(employee_benefits));
                    statement.setBigDecimal(3,  parseMoney(social_benefits));
                    statement.setBigDecimal(4,  parseMoney(transfers));
                    statement.setBigDecimal(5,  parseMoney(purchases_of_goods_and_services));
                    statement.setBigDecimal(6,  parseMoney(subsidies));
                    statement.setBigDecimal(7,  parseMoney(interest));
                    statement.setBigDecimal(8,  parseMoney(other_expenditures));
                    statement.setBigDecimal(9,  parseMoney(appropriations));
                    statement.setBigDecimal(10, parseMoney(fixed_assets));
                    statement.setBigDecimal(11, parseMoney(valuables));
                    statement.setBigDecimal(12, parseMoney(loans));
                    statement.setBigDecimal(13, parseMoney(equity_securities_and_fund_shares));
                    statement.setBigDecimal(14, parseMoney(debt_securities_liabilities));
                    statement.setBigDecimal(15, parseMoney(loans_liabilities));
                    statement.setBigDecimal(16, parseMoney(financial_derivatives));

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

     void insertMinistries2026() throws Exception {
String sql = "INSERT INTO ministries_2026(total_ministries,presidency_of_the_republic,hellenic_parliament,presidency_of_the_government,ministry_of_interior,ministry_of_foreign_affairs,ministry_of_national_defence,ministry_of_health,ministry_of_justice,ministry_of_education_religious_affairs_and_sports,ministry_of_culture,ministry_of_national_economy_and_finance,ministry_of_agricultural_development_and_food,ministry_of_environment_and_energy,ministry_of_labor_and_social_security,ministry_of_social_cohesion_and_family,ministry_of_development,ministry_of_infrastructure_and_transport,ministry_of_maritime_affairs_and_insular_policy,ministry_of_tourism,ministry_of_digital_governance,ministry_of_migration_and_asylum,ministry_of_citizen_protection,ministry_of_climate_crisis_and_civil_protection) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        

        try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2026.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2026.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
            String total_ministries=null;
            String presidency_of_the_republic=null;
            String hellenic_parliament=null;
            String presidency_of_the_government=null;
            String ministry_of_interior=null;
            String ministry_of_foreign_affairs=null;
            String ministry_of_national_defence=null;
            String ministry_of_health=null;
            String ministry_of_justice=null;
            String ministry_of_education_religious_affairs_and_sports=null;
            String ministry_of_culture=null;
            String ministry_of_national_economy_and_finance=null;
            String ministry_of_agricultural_development_and_food=null;
            String ministry_of_environment_and_energy=null;
            String ministry_of_labor_and_social_security=null;
            String ministry_of_social_cohesion_and_family=null;
            String ministry_of_development=null;
            String ministry_of_infrastructure_and_transport=null;
            String ministry_of_maritime_affairs_and_insular_policy=null;
            String ministry_of_tourism=null;
            String ministry_of_digital_governance=null;
            String ministry_of_migration_and_asylum=null;
            String ministry_of_citizen_protection=null;
            String ministry_of_climate_crisis_and_civil_protection=null;
            



            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 55) {
                    continue;
                }
                if (lineNumber > 78) {
                    break;
                }
               String value = (data.length > 5) ? data[5] : "0";

              if (lineNumber == 55) {
                presidency_of_the_republic = value;
            } else if (lineNumber == 56) {
                hellenic_parliament = value;
            } else if (lineNumber == 57) {
                presidency_of_the_government = value;
            } else if (lineNumber == 58) {
                total_ministries = data[4];
            } else if (lineNumber == 59) {
                ministry_of_interior = value;
            } else if (lineNumber == 60) {
                ministry_of_foreign_affairs = value;
            } else if (lineNumber == 61) {
                ministry_of_national_defence = value;
            } else if (lineNumber == 62) {
                ministry_of_health = value;
            } else if (lineNumber == 63) {
                ministry_of_justice = value;
            } else if (lineNumber == 64) {
                ministry_of_education_religious_affairs_and_sports = data[6];
            } else if (lineNumber == 65) {
                ministry_of_culture = value;
            } else if (lineNumber == 66) {
                ministry_of_national_economy_and_finance = value;
            } else if (lineNumber == 67) {
                ministry_of_agricultural_development_and_food = value;
            } else if (lineNumber == 68) {
                ministry_of_environment_and_energy = value;
            } else if (lineNumber == 69) {
                ministry_of_labor_and_social_security = value;
            } else if (lineNumber == 70) {
                ministry_of_social_cohesion_and_family = value;
            } else if (lineNumber == 71) {
                ministry_of_development = value;
            } else if (lineNumber == 72) {
                ministry_of_infrastructure_and_transport = value;
            } else if (lineNumber == 73) {
                ministry_of_maritime_affairs_and_insular_policy = value;
            } else if (lineNumber == 74) {
                ministry_of_tourism = value;
            } else if (lineNumber == 75) {
                ministry_of_digital_governance = value;
            } else if (lineNumber == 76) {
                ministry_of_migration_and_asylum = value;
            } else if (lineNumber == 77) {
                ministry_of_citizen_protection = value;
            } else if (lineNumber == 78) {
                ministry_of_climate_crisis_and_civil_protection = value;
            }


            }
            statement.setBigDecimal(1,  parseMoney(total_ministries));
            statement.setBigDecimal(2,  parseMoney(presidency_of_the_republic));
            statement.setBigDecimal(3,  parseMoney(hellenic_parliament));
            statement.setBigDecimal(4,  parseMoney(presidency_of_the_government));
            statement.setBigDecimal(5,  parseMoney(ministry_of_interior));
            statement.setBigDecimal(6,  parseMoney(ministry_of_foreign_affairs));
            statement.setBigDecimal(7,  parseMoney(ministry_of_national_defence));
            statement.setBigDecimal(8,  parseMoney(ministry_of_health));
            statement.setBigDecimal(9,  parseMoney(ministry_of_justice));
            statement.setBigDecimal(10, parseMoney(ministry_of_education_religious_affairs_and_sports));
            statement.setBigDecimal(11, parseMoney(ministry_of_culture));
            statement.setBigDecimal(12, parseMoney(ministry_of_national_economy_and_finance));
            statement.setBigDecimal(13, parseMoney(ministry_of_agricultural_development_and_food));
            statement.setBigDecimal(14, parseMoney(ministry_of_environment_and_energy));
            statement.setBigDecimal(15, parseMoney(ministry_of_labor_and_social_security));
            statement.setBigDecimal(16, parseMoney(ministry_of_social_cohesion_and_family));
            statement.setBigDecimal(17, parseMoney(ministry_of_development));
            statement.setBigDecimal(18, parseMoney(ministry_of_infrastructure_and_transport));
            statement.setBigDecimal(19, parseMoney(ministry_of_maritime_affairs_and_insular_policy));
            statement.setBigDecimal(20, parseMoney(ministry_of_tourism));
            statement.setBigDecimal(21, parseMoney(ministry_of_digital_governance));
            statement.setBigDecimal(22, parseMoney(ministry_of_migration_and_asylum));
            statement.setBigDecimal(23, parseMoney(ministry_of_citizen_protection));
            statement.setBigDecimal(24, parseMoney(ministry_of_climate_crisis_and_civil_protection));

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    void insertDecentralizedAdministrations2026() throws Exception {
String sql = "INSERT INTO decentralized_administrations_2026(total_da,decentralized_administration_of_attica,decentralized_administration_of_thessaly_central_greece,decentralized_administration_of_epirus_western_macedonia,decentralized_administration_of_peloponnese_western_greece_and_ionian,decentralized_administration_of_aegean,decentralized_administration_of_crete,decentralized_administration_of_macedonia_thrace) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        
       try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2026.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2026.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;

            String total_da=null;
            String decentralized_administration_of_attica=null;
            String decentralized_administration_of_thessaly_central_greece=null;
            String decentralized_administration_of_epirus_western_macedonia=null;
            String decentralized_administration_of_peloponnese_western_greece_and_ionian=null;
            String decentralized_administration_of_aegean=null;
            String decentralized_administration_of_crete=null;
            String decentralized_administration_of_macedonia_thrace=null;
           
           

            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 79) {
                    continue;
                }
                if (lineNumber > 86) {
                    break;
                }
                if (lineNumber == 79){
                    total_da = data[2];
                } else if (lineNumber == 80) {
                    decentralized_administration_of_attica = data[3];
                } else if (lineNumber == 81) {
                    decentralized_administration_of_thessaly_central_greece = data[3];
                } else if (lineNumber == 82) {
                    decentralized_administration_of_epirus_western_macedonia = data[3];
                } else if (lineNumber == 83) {
                    decentralized_administration_of_peloponnese_western_greece_and_ionian = data[3];
                } else if (lineNumber == 84) {
                    decentralized_administration_of_aegean = data[3];
                } else if (lineNumber == 85) {
                    decentralized_administration_of_crete = data[3];
                } else if (lineNumber == 86) {
                    decentralized_administration_of_macedonia_thrace = data[3];
                }


            }
            statement.setBigDecimal(1, parseMoney(total_da));
            statement.setBigDecimal(2, parseMoney(decentralized_administration_of_attica));
            statement.setBigDecimal(3, parseMoney(decentralized_administration_of_thessaly_central_greece));
            statement.setBigDecimal(4, parseMoney(decentralized_administration_of_epirus_western_macedonia));
            statement.setBigDecimal(5, parseMoney(decentralized_administration_of_peloponnese_western_greece_and_ionian));
            statement.setBigDecimal(6, parseMoney(decentralized_administration_of_aegean));
            statement.setBigDecimal(7, parseMoney(decentralized_administration_of_crete));
            statement.setBigDecimal(8, parseMoney(decentralized_administration_of_macedonia_thrace));


                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


     static void insertBudgetSummary2026() throws Exception {
String sql = "INSERT INTO budget_summary_2026(budget_result,total_revenue,total_expenses,total_ministries,total_da) VALUES(?, ?, ?, ?, ?)";
        
        try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2026.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2026.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;

            String budget_result=null;
            String total_revenue=null;
            String total_expenses=null;
            String total_ministries=null;
            String total_da=null;

            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber == 44) {
                    budget_result=data[3];
                } else if (lineNumber ==14) {
                    total_revenue=data[3];
                } else if (lineNumber == 28) {
                    total_expenses=data[3];
                } else if (lineNumber ==58) {
                    total_ministries=data[4];
                } else if (lineNumber ==79 ) {
                    total_da=data[2];
                }
                    
                

            }
            statement.setBigDecimal(1, parseMoney(budget_result));
            statement.setBigDecimal(2, parseMoney(total_revenue));
            statement.setBigDecimal(3, parseMoney(total_expenses));
            statement.setBigDecimal(4, parseMoney(total_ministries));
            statement.setBigDecimal(5, parseMoney(total_da));
           

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------ 2025 ------------------ //

    void insertRevenue2025() throws Exception {
         try {
            Connection connection = DatabaseConnection.getConnection();

            String sql = "INSERT INTO revenue_2025(total_revenue,taxes,social_contributions,transfers,sales_of_goods_and_services,other_current_revenue,fixed_assets,debt_securities,loans,equity_securities_and_fund_shares,currency_and_deposit_liabilities,debt_securities_liabilities,loans_liabilities,financial_derivatives) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2025.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2025.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
             String total_revenue = null;
                String taxes = null;
                String social_contributions = null;
                String transfers = null;
                String sales_of_goods_and_services = null;
                String other_current_revenue = null;
                String fixed_assets = null;
                String debt_securities = null;
                String loans = null;
                String equity_securities_and_fund_shares = null;
                String currency_and_deposit_liabilities = null;
                String debt_securities_liabilities = null;
                String loans_liabilities = null;
                String financial_derivatives = null;
            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");


                if (lineNumber < 13) {
                    continue;
                }
                if (lineNumber > 26) {
                    break;
                }
               
                if (lineNumber == 13) {
                    total_revenue = data[3];
                } else if (lineNumber == 14) {
                    taxes = data[3];
                } else if (lineNumber == 15) {
                    social_contributions = data[3];
                } else if (lineNumber == 16) {
                    transfers = data[3];
                } else if (lineNumber == 17) {
                    sales_of_goods_and_services = data[3];
                } else if (lineNumber == 18) {
                    other_current_revenue = data[3];
                } else if (lineNumber == 19) {
                    fixed_assets = data[3];
                } else if (lineNumber == 20) {
                    debt_securities = data[3];
                } else if (lineNumber == 21) {
                    loans = data[3];
                } else if (lineNumber == 22) {
                    equity_securities_and_fund_shares = data[3];
                } else if (lineNumber == 23) {
                    currency_and_deposit_liabilities = data[3];
                } else if (lineNumber == 24) {
                    debt_securities_liabilities = data[3];
                } else if (lineNumber == 25) {
                    loans_liabilities = data[3];
                } else if (lineNumber == 26) {
                    financial_derivatives = data[3];
                }

            }
             statement.setBigDecimal(1,  parseMoney(total_revenue));
                    statement.setBigDecimal(2,  parseMoney(taxes));
                    statement.setBigDecimal(3,  parseMoney(social_contributions));
                    statement.setBigDecimal(4,  parseMoney(transfers));
                    statement.setBigDecimal(5,  parseMoney(sales_of_goods_and_services));
                    statement.setBigDecimal(6,  parseMoney(other_current_revenue));
                    statement.setBigDecimal(7,  parseMoney(fixed_assets));
                    statement.setBigDecimal(8,  parseMoney(debt_securities));
                    statement.setBigDecimal(9,  parseMoney(loans));
                    statement.setBigDecimal(10, parseMoney(equity_securities_and_fund_shares));
                    statement.setBigDecimal(11, parseMoney(currency_and_deposit_liabilities));
                    statement.setBigDecimal(12, parseMoney(debt_securities_liabilities));
                    statement.setBigDecimal(13, parseMoney(loans_liabilities));
                    statement.setBigDecimal(14, parseMoney(financial_derivatives));

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
      
    }
    private static BigDecimal parseMoney(String s) {
    if (s == null || s.isBlank()) return BigDecimal.ZERO;
    String clean = s.trim().replace(".", "").replace(",", ".");
    return new BigDecimal(clean);
}




    void insertExpenses2025() throws Exception {
String sql = "INSERT INTO expenses_2025(total_expenses,employee_benefits,social_benefits,transfers,purchases_of_goods_and_services,subsidies,interest,other_expenditures,appropriations,fixed_assets,valuables,loans,equity_securities_and_fund_shares,debt_securities_liabilities,loans_liabilities) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2025.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2025.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
            String total_expenses=null;
            String employee_benefits=null;
            String social_benefits=null;
            String transfers=null;
            String purchases_of_goods_and_services=null;
            String subsidies=null;
            String interest=null;
            String other_expenditures=null;
            String appropriations=null;
            String fixed_assets=null;
            String valuables=null;
            String loans=null;
            String equity_securities_and_fund_shares=null;
            String debt_securities_liabilities=null;
            String loans_liabilities=null;


            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 27) {
                    continue;
                }
                if (lineNumber > 41) {
                    break;
                }
               
                if (lineNumber == 27) {
                    total_expenses=data[3];
                } else if (lineNumber == 28) {
                    employee_benefits=data[3];
                } else if (lineNumber == 29) {
                    social_benefits=data[3];
                } else if (lineNumber == 30) {
                    transfers=data[3];
                } else if (lineNumber == 31) {
                    purchases_of_goods_and_services=data[3];
                } else if (lineNumber == 32) {
                    subsidies=data[3];
                } else if (lineNumber == 33) {
                    interest=data[3];
                } else if (lineNumber == 34) {
                    other_expenditures=data[3];
                } else if (lineNumber == 35) {
                    appropriations=data[3];
                } else if (lineNumber == 36) {
                    fixed_assets=data[3];
                } else if (lineNumber == 37) {
                    valuables=data[3];
                } else if (lineNumber == 38) {
                    loans=data[3];
                } else if (lineNumber == 39) {
                    equity_securities_and_fund_shares=data[3];
                } else if (lineNumber == 40) {
                    debt_securities_liabilities=data[3];
                } else if (lineNumber == 41) {
                    loans_liabilities=data[3];
                }
            }
             statement.setBigDecimal(1,  parseMoney(total_expenses));
                    statement.setBigDecimal(2,  parseMoney(employee_benefits));
                    statement.setBigDecimal(3,  parseMoney(social_benefits));
                    statement.setBigDecimal(4,  parseMoney(transfers));
                    statement.setBigDecimal(5,  parseMoney(purchases_of_goods_and_services));
                    statement.setBigDecimal(6,  parseMoney(subsidies));
                    statement.setBigDecimal(7,  parseMoney(interest));
                    statement.setBigDecimal(8,  parseMoney(other_expenditures));
                    statement.setBigDecimal(9,  parseMoney(appropriations));
                    statement.setBigDecimal(10, parseMoney(fixed_assets));
                    statement.setBigDecimal(11, parseMoney(valuables));
                    statement.setBigDecimal(12, parseMoney(loans));
                    statement.setBigDecimal(13, parseMoney(equity_securities_and_fund_shares));
                    statement.setBigDecimal(14, parseMoney(debt_securities_liabilities));
                    statement.setBigDecimal(15, parseMoney(loans_liabilities));

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


      void insertMinistries2025() throws Exception {
String sql = "INSERT INTO ministries_2025(total_ministries,presidency_of_the_republic,hellenic_parliament,presidency_of_the_government,ministry_of_interior,ministry_of_foreign_affairs,ministry_of_national_defence,ministry_of_health,ministry_of_justice,ministry_of_education_religious_affairs_and_sports,ministry_of_culture,ministry_of_national_economy_and_finance,ministry_of_agricultural_development_and_food,ministry_of_environment_and_energy,ministry_of_labor_and_social_security,ministry_of_social_cohesion_and_family,ministry_of_development,ministry_of_infrastructure_and_transport,ministry_of_maritime_affairs_and_insular_policy,ministry_of_tourism,ministry_of_digital_governance,ministry_of_migration_and_asylum,ministry_of_citizen_protection,ministry_of_climate_crisis_and_civil_protection) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        

        try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2025.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2025.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
            String total_ministries=null;
            String presidency_of_the_republic=null;
            String hellenic_parliament=null;
            String presidency_of_the_government=null;
            String ministry_of_interior=null;
            String ministry_of_foreign_affairs=null;
            String ministry_of_national_defence=null;
            String ministry_of_health=null;
            String ministry_of_justice=null;
            String ministry_of_education_religious_affairs_and_sports=null;
            String ministry_of_culture=null;
            String ministry_of_national_economy_and_finance=null;
            String ministry_of_agricultural_development_and_food=null;
            String ministry_of_environment_and_energy=null;
            String ministry_of_labor_and_social_security=null;
            String ministry_of_social_cohesion_and_family=null;
            String ministry_of_development=null;
            String ministry_of_infrastructure_and_transport=null;
            String ministry_of_maritime_affairs_and_insular_policy=null;
            String ministry_of_tourism=null;
            String ministry_of_digital_governance=null;
            String ministry_of_migration_and_asylum=null;
            String ministry_of_citizen_protection=null;
            String ministry_of_climate_crisis_and_civil_protection=null;
            



            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 53) {
                    continue;
                }
                if (lineNumber > 76) {
                    break;
                }
               String value = (data.length > 5) ? data[5] : "0";

                if (lineNumber == 53) {
                    presidency_of_the_republic = value;
                } else if (lineNumber == 54) {
                    hellenic_parliament = value;
                } else if (lineNumber == 55) {
                    presidency_of_the_government = value;
                } else if (lineNumber == 56) {
                    total_ministries = data[4];
                } else if (lineNumber == 57) {
                    ministry_of_interior = value;
                } else if (lineNumber == 58) {
                    ministry_of_foreign_affairs = value;
                } else if (lineNumber == 59) {
                    ministry_of_national_defence = value;
                } else if (lineNumber == 60) {
                    ministry_of_health = value;
                } else if (lineNumber == 61) {
                    ministry_of_justice = value;
                } else if (lineNumber == 62) {
                    ministry_of_education_religious_affairs_and_sports = value;
                } else if (lineNumber == 63) {
                    ministry_of_culture = value;
                } else if (lineNumber == 64) {
                    ministry_of_national_economy_and_finance = value;
                } else if (lineNumber == 65) {
                    ministry_of_agricultural_development_and_food = value;
                } else if (lineNumber == 66) {
                    ministry_of_environment_and_energy = value;
                } else if (lineNumber == 67) {
                    ministry_of_labor_and_social_security = value;
                } else if (lineNumber == 68) {
                    ministry_of_social_cohesion_and_family = value;
                } else if (lineNumber == 69) {
                    ministry_of_development = value;
                } else if (lineNumber == 70) {
                    ministry_of_infrastructure_and_transport = value;
                } else if (lineNumber == 71) {
                    ministry_of_maritime_affairs_and_insular_policy = value;
                } else if (lineNumber == 72) {
                    ministry_of_tourism = value;
                } else if (lineNumber == 73) {
                    ministry_of_digital_governance = value;
                } else if (lineNumber == 74) {
                    ministry_of_migration_and_asylum = value;
                } else if (lineNumber == 75) {
                    ministry_of_citizen_protection = value;
                } else if (lineNumber == 76) {
                    ministry_of_climate_crisis_and_civil_protection = value;
                }


            }
            statement.setBigDecimal(1,  parseMoney(total_ministries));
            statement.setBigDecimal(2,  parseMoney(presidency_of_the_republic));
            statement.setBigDecimal(3,  parseMoney(hellenic_parliament));
            statement.setBigDecimal(4,  parseMoney(presidency_of_the_government));
            statement.setBigDecimal(5,  parseMoney(ministry_of_interior));
            statement.setBigDecimal(6,  parseMoney(ministry_of_foreign_affairs));
            statement.setBigDecimal(7,  parseMoney(ministry_of_national_defence));
            statement.setBigDecimal(8,  parseMoney(ministry_of_health));
            statement.setBigDecimal(9,  parseMoney(ministry_of_justice));
            statement.setBigDecimal(10, parseMoney(ministry_of_education_religious_affairs_and_sports));
            statement.setBigDecimal(11, parseMoney(ministry_of_culture));
            statement.setBigDecimal(12, parseMoney(ministry_of_national_economy_and_finance));
            statement.setBigDecimal(13, parseMoney(ministry_of_agricultural_development_and_food));
            statement.setBigDecimal(14, parseMoney(ministry_of_environment_and_energy));
            statement.setBigDecimal(15, parseMoney(ministry_of_labor_and_social_security));
            statement.setBigDecimal(16, parseMoney(ministry_of_social_cohesion_and_family));
            statement.setBigDecimal(17, parseMoney(ministry_of_development));
            statement.setBigDecimal(18, parseMoney(ministry_of_infrastructure_and_transport));
            statement.setBigDecimal(19, parseMoney(ministry_of_maritime_affairs_and_insular_policy));
            statement.setBigDecimal(20, parseMoney(ministry_of_tourism));
            statement.setBigDecimal(21, parseMoney(ministry_of_digital_governance));
            statement.setBigDecimal(22, parseMoney(ministry_of_migration_and_asylum));
            statement.setBigDecimal(23, parseMoney(ministry_of_citizen_protection));
            statement.setBigDecimal(24, parseMoney(ministry_of_climate_crisis_and_civil_protection));

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



     void insertDecentralizedAdministrations2025() throws Exception {
String sql = "INSERT INTO decentralized_administrations_2025(total_da,decentralized_administration_of_attica,decentralized_administration_of_thessaly_central_greece,decentralized_administration_of_epirus_western_macedonia,decentralized_administration_of_peloponnese_western_greece_and_ionian,decentralized_administration_of_aegean,decentralized_administration_of_crete,decentralized_administration_of_macedonia_thrace) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        
       try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2025.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2025.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;

            String total_da=null;
            String decentralized_administration_of_attica=null;
            String decentralized_administration_of_thessaly_central_greece=null;
            String decentralized_administration_of_epirus_western_macedonia=null;
            String decentralized_administration_of_peloponnese_western_greece_and_ionian=null;
            String decentralized_administration_of_aegean=null;
            String decentralized_administration_of_crete=null;
            String decentralized_administration_of_macedonia_thrace=null;
           
           

            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 77) {
                    continue;
                }
                if (lineNumber > 84) {
                    break;
                }
                if (lineNumber ==77){
                    total_da=data[2];
                }else if (lineNumber == 78) {
                    decentralized_administration_of_attica=data[3];
                } else if (lineNumber == 79) {
                    decentralized_administration_of_thessaly_central_greece=data[3];
                } else if (lineNumber == 80) {
                    decentralized_administration_of_epirus_western_macedonia=data[3];
                } else if (lineNumber == 81) {
                    decentralized_administration_of_peloponnese_western_greece_and_ionian=data[3];
                } else if (lineNumber == 82) {
                    decentralized_administration_of_aegean=data[3];
                } else if (lineNumber == 83) {
                    decentralized_administration_of_crete=data[3];
                } else if (lineNumber == 84) {
                    decentralized_administration_of_macedonia_thrace=data[3];
                } 

            }
            statement.setBigDecimal(1, parseMoney(total_da));
            statement.setBigDecimal(2, parseMoney(decentralized_administration_of_attica));
            statement.setBigDecimal(3, parseMoney(decentralized_administration_of_thessaly_central_greece));
            statement.setBigDecimal(4, parseMoney(decentralized_administration_of_epirus_western_macedonia));
            statement.setBigDecimal(5, parseMoney(decentralized_administration_of_peloponnese_western_greece_and_ionian));
            statement.setBigDecimal(6, parseMoney(decentralized_administration_of_aegean));
            statement.setBigDecimal(7, parseMoney(decentralized_administration_of_crete));
            statement.setBigDecimal(8, parseMoney(decentralized_administration_of_macedonia_thrace));


                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


     static void insertBudgetSummary2025() throws Exception {
String sql = "INSERT INTO budget_summary_2025(budget_result,total_revenue,total_expenses,total_ministries,total_da) VALUES(?, ?, ?, ?, ?)";
        
        try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2025.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2025.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;

            String budget_result=null;
            String total_revenue=null;
            String total_expenses=null;
            String total_ministries=null;
            String total_da=null;

            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber == 42) {
                    budget_result=data[3];
                } else if (lineNumber ==13) {
                    total_revenue=data[3];
                } else if (lineNumber == 27) {
                    total_expenses=data[3];
                } else if (lineNumber ==56) {
                    total_ministries=data[4];
                } else if (lineNumber ==77 ) {
                    total_da=data[2];
                }
                    
                

            }
            statement.setBigDecimal(1, parseMoney(budget_result));
            statement.setBigDecimal(2, parseMoney(total_revenue));
            statement.setBigDecimal(3, parseMoney(total_expenses));
            statement.setBigDecimal(4, parseMoney(total_ministries));
            statement.setBigDecimal(5, parseMoney(total_da));
           

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ------------------ 2024 ------------------ //

     void insertRevenue2024() throws Exception {
String sql = "INSERT INTO revenue_2024(total_revenue,taxes,social_contributions,transfers,sales_of_goods_and_services,other_current_revenue,fixed_assets,debt_securities,equity_securities_and_fund_shares,currency_and_deposit_liabilities,debt_securities_liabilities,loans_liabilities,financial_derivatives) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
       
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            

             File csvFile = new File("proipologismos2024.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2024.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
             String total_revenue = null;
                String taxes = null;
                String social_contributions = null;
                String transfers = null;
                String sales_of_goods_and_services = null;
                String other_current_revenue = null;
                String fixed_assets = null;
                String debt_securities = null;
                String equity_securities_and_fund_shares = null;
                String currency_and_deposit_liabilities = null;
                String debt_securities_liabilities = null;
                String loans = null;
                String financial_derivatives = null;
            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 13) {
                    continue;
                }
                if (lineNumber > 25) {
                    break;
                }
               
                if (lineNumber==13){
                     total_revenue=data[3];
                } else if (lineNumber == 14) {
                     taxes=data[3];
                } else if (lineNumber == 15) {
                     social_contributions=data[3];
                } else if (lineNumber == 16) {
                     transfers=data[3];
                } else if (lineNumber == 17) {
                     sales_of_goods_and_services=data[3];
                } else if (lineNumber == 18) {
                     other_current_revenue=data[3];
                } else if (lineNumber == 19) {
                     fixed_assets=data[3];
                } else if (lineNumber == 20) {
                     debt_securities=data[3];
                } else if (lineNumber == 21) {
                    equity_securities_and_fund_shares=data[3];
                } else if (lineNumber == 22) {
                    currency_and_deposit_liabilities=data[3];
                } else if (lineNumber == 23) {
                    debt_securities_liabilities=data[3];
                } else if (lineNumber == 24) {
                    loans=data[3];
                } else if (lineNumber == 25) {
                   financial_derivatives=data[3];
                }

            }
             statement.setBigDecimal(1,  parseMoney(total_revenue));
                    statement.setBigDecimal(2,  parseMoney(taxes));
                    statement.setBigDecimal(3,  parseMoney(social_contributions));
                    statement.setBigDecimal(4,  parseMoney(transfers));
                    statement.setBigDecimal(5,  parseMoney(sales_of_goods_and_services));
                    statement.setBigDecimal(6,  parseMoney(other_current_revenue));
                    statement.setBigDecimal(7,  parseMoney(fixed_assets));
                    statement.setBigDecimal(8,  parseMoney(debt_securities));
                    statement.setBigDecimal(9,  parseMoney(equity_securities_and_fund_shares));
                    statement.setBigDecimal(10, parseMoney(currency_and_deposit_liabilities));
                    statement.setBigDecimal(11, parseMoney(debt_securities_liabilities));
                    statement.setBigDecimal(12, parseMoney(loans));
                    statement.setBigDecimal(13, parseMoney(financial_derivatives));
                    

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


     void insertExpenses2024() throws Exception {
String sql = "INSERT INTO expenses_2024(total_expenses,employee_benefits,social_benefits,transfers,purchases_of_goods_and_services,subsidies,interest,other_expenditures,appropriations,fixed_assets,valuables,loans,equity_securities_and_fund_shares,debt_securities_liabilities,loans_liabilities) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2024.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2024.csv στον τρέχοντα φάκελο");
                return;
            }

            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
            String total_expenses=null;
            String employee_benefits=null;
            String social_benefits=null;
            String transfers=null;
            String purchases_of_goods_and_services=null;
            String subsidies=null;
            String interest=null;
            String other_expenditures=null;
            String appropriations=null;
            String fixed_assets=null;
            String valuables=null;
            String loans=null;
            String equity_securities_and_fund_shares=null;
            String debt_securities_liabilities=null;
            String loans_liabilities=null;


            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 26) {
                    continue;
                }
                if (lineNumber > 40) {
                    break;
                }
               
                if (lineNumber == 26) {
                    total_expenses=data[3];
                } else if (lineNumber == 27) {
                    employee_benefits=data[3];
                } else if (lineNumber == 28) {
                    social_benefits=data[3];
                } else if (lineNumber == 29) {
                    transfers=data[3];
                } else if (lineNumber == 30) {
                    purchases_of_goods_and_services=data[3];
                } else if (lineNumber == 31) {
                    subsidies=data[3];
                } else if (lineNumber == 32) {
                    interest=data[3];
                } else if (lineNumber == 33) {
                    other_expenditures=data[3];
                } else if (lineNumber == 34) {
                    appropriations=data[3];
                } else if (lineNumber == 35) {
                    fixed_assets=data[3];
                } else if (lineNumber == 36) {
                    valuables=data[3];
                } else if (lineNumber == 37) {
                    loans=data[3];
                } else if (lineNumber == 38) {
                    equity_securities_and_fund_shares=data[3];
                } else if (lineNumber == 39) {
                    debt_securities_liabilities=data[3];
                } else if (lineNumber == 40) {
                    loans_liabilities=data[3];
                }
            }
             statement.setBigDecimal(1,  parseMoney(total_expenses));
                    statement.setBigDecimal(2,  parseMoney(employee_benefits));
                    statement.setBigDecimal(3,  parseMoney(social_benefits));
                    statement.setBigDecimal(4,  parseMoney(transfers));
                    statement.setBigDecimal(5,  parseMoney(purchases_of_goods_and_services));
                    statement.setBigDecimal(6,  parseMoney(subsidies));
                    statement.setBigDecimal(7,  parseMoney(interest));
                    statement.setBigDecimal(8,  parseMoney(other_expenditures));
                    statement.setBigDecimal(9,  parseMoney(appropriations));
                    statement.setBigDecimal(10, parseMoney(fixed_assets));
                    statement.setBigDecimal(11, parseMoney(valuables));
                    statement.setBigDecimal(12, parseMoney(loans));
                    statement.setBigDecimal(13, parseMoney(equity_securities_and_fund_shares));
                    statement.setBigDecimal(14, parseMoney(debt_securities_liabilities));
                    statement.setBigDecimal(15, parseMoney(loans_liabilities));

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


     static void insertMinistries2024() throws Exception {
String sql = "INSERT INTO ministries_2024(total_ministries,presidency_of_the_republic,hellenic_parliament,presidency_of_the_government,ministry_of_interior,ministry_of_foreign_affairs,ministry_of_national_defence,ministry_of_health,ministry_of_justice,ministry_of_education_religious_affairs_and_sports,ministry_of_culture,ministry_of_national_economy_and_finance,ministry_of_agricultural_development_and_food,ministry_of_environment_and_energy,ministry_of_labor_and_social_security,ministry_of_social_cohesion_and_family,ministry_of_development,ministry_of_infrastructure_and_transport,ministry_of_maritime_affairs_and_insular_policy,ministry_of_tourism,ministry_of_digital_governance,ministry_of_migration_and_asylum,ministry_of_citizen_protection,ministry_of_climate_crisis_and_civil_protection) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
       
        try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2024.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2024.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
            String total_ministries=null;
            String presidency_of_the_republic=null;
            String hellenic_parliament=null;
            String presidency_of_the_government=null;
            String ministry_of_interior=null;
            String ministry_of_foreign_affairs=null;
            String ministry_of_national_defence=null;
            String ministry_of_health=null;
            String ministry_of_justice=null;
            String ministry_of_education_religious_affairs_and_sports=null;
            String ministry_of_culture=null;
            String ministry_of_national_economy_and_finance=null;
            String ministry_of_agricultural_development_and_food=null;
            String ministry_of_environment_and_energy=null;
            String ministry_of_labor_and_social_security=null;
            String ministry_of_social_cohesion_and_family=null;
            String ministry_of_development=null;
            String ministry_of_infrastructure_and_transport=null;
            String ministry_of_maritime_affairs_and_insular_policy=null;
            String ministry_of_tourism=null;
            String ministry_of_digital_governance=null;
            String ministry_of_migration_and_asylum=null;
            String ministry_of_citizen_protection=null;
            String ministry_of_climate_crisis_and_civil_protection=null;
            



            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 52) {
                    continue;
                }
                if (lineNumber > 75) {
                    break;
                }
               String value = (data.length > 5) ? data[5] : "0";

                if (lineNumber == 52) {
                    presidency_of_the_republic = value;
                } else if (lineNumber == 53) {
                    hellenic_parliament = value;
                } else if (lineNumber == 54) {
                    presidency_of_the_government = value;
                } else if (lineNumber == 55) {
                    total_ministries = data[4];
                } else if (lineNumber == 56) {
                    ministry_of_interior = value;
                } else if (lineNumber == 57) {
                    ministry_of_foreign_affairs = value;
                } else if (lineNumber == 58) {
                    ministry_of_national_defence =data[4];
                } else if (lineNumber == 59) {
                    ministry_of_health = data[4];
                } else if (lineNumber == 60) {
                    ministry_of_justice = data[4];
                } else if (lineNumber == 61) {
                    ministry_of_education_religious_affairs_and_sports = value;
                } else if (lineNumber == 62) {
                    ministry_of_culture = data[4];
                } else if (lineNumber == 63) {
                    ministry_of_national_economy_and_finance = data[4];
                } else if (lineNumber == 64) {
                    ministry_of_agricultural_development_and_food = data[4];
                } else if (lineNumber == 65) {
                    ministry_of_environment_and_energy = data[4];
                } else if (lineNumber == 66) {
                    ministry_of_labor_and_social_security = data[4];
                } else if (lineNumber == 67) {
                    ministry_of_social_cohesion_and_family = data[4];
                } else if (lineNumber == 68) {
                    ministry_of_development = data[4];
                } else if (lineNumber == 69) {
                    ministry_of_infrastructure_and_transport = data[4];
                } else if (lineNumber == 70) {
                    ministry_of_maritime_affairs_and_insular_policy = data[4];
                } else if (lineNumber == 71) {
                    ministry_of_tourism = data[4];
                } else if (lineNumber == 72) {
                    ministry_of_digital_governance = data[4];
                } else if (lineNumber == 73) {
                    ministry_of_migration_and_asylum = data[4];
                } else if (lineNumber == 74) {
                    ministry_of_citizen_protection = value;
                } else if (lineNumber == 75) {
                    ministry_of_climate_crisis_and_civil_protection = data[4];
                }



            }
            statement.setBigDecimal(1,  parseMoney(total_ministries));
            statement.setBigDecimal(2,  parseMoney(presidency_of_the_republic));
            statement.setBigDecimal(3,  parseMoney(hellenic_parliament));
            statement.setBigDecimal(4,  parseMoney(presidency_of_the_government));
            statement.setBigDecimal(5,  parseMoney(ministry_of_interior));
            statement.setBigDecimal(6,  parseMoney(ministry_of_foreign_affairs));
            statement.setBigDecimal(7,  parseMoney(ministry_of_national_defence));
            statement.setBigDecimal(8,  parseMoney(ministry_of_health));
            statement.setBigDecimal(9,  parseMoney(ministry_of_justice));
            statement.setBigDecimal(10, parseMoney(ministry_of_education_religious_affairs_and_sports));
            statement.setBigDecimal(11, parseMoney(ministry_of_culture));
            statement.setBigDecimal(12, parseMoney(ministry_of_national_economy_and_finance));
            statement.setBigDecimal(13, parseMoney(ministry_of_agricultural_development_and_food));
            statement.setBigDecimal(14, parseMoney(ministry_of_environment_and_energy));
            statement.setBigDecimal(15, parseMoney(ministry_of_labor_and_social_security));
            statement.setBigDecimal(16, parseMoney(ministry_of_social_cohesion_and_family));
            statement.setBigDecimal(17, parseMoney(ministry_of_development));
            statement.setBigDecimal(18, parseMoney(ministry_of_infrastructure_and_transport));
            statement.setBigDecimal(19, parseMoney(ministry_of_maritime_affairs_and_insular_policy));
            statement.setBigDecimal(20, parseMoney(ministry_of_tourism));
            statement.setBigDecimal(21, parseMoney(ministry_of_digital_governance));
            statement.setBigDecimal(22, parseMoney(ministry_of_migration_and_asylum));
            statement.setBigDecimal(23, parseMoney(ministry_of_citizen_protection));
            statement.setBigDecimal(24, parseMoney(ministry_of_climate_crisis_and_civil_protection));

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    static void insertDecentralizedAdministrations2024() throws Exception {
String sql = "INSERT INTO decentralized_administrations_2024(total_da,decentralized_administration_of_attica,decentralized_administration_of_thessaly_central_greece,decentralized_administration_of_epirus_western_macedonia,decentralized_administration_of_peloponnese_western_greece_and_ionian,decentralized_administration_of_aegean,decentralized_administration_of_crete,decentralized_administration_of_macedonia_thrace) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        

         try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2024.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2024.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;

            String total_da=null;
            String decentralized_administration_of_attica=null;
            String decentralized_administration_of_thessaly_central_greece=null;
            String decentralized_administration_of_epirus_western_macedonia=null;
            String decentralized_administration_of_peloponnese_western_greece_and_ionian=null;
            String decentralized_administration_of_aegean=null;
            String decentralized_administration_of_crete=null;
            String decentralized_administration_of_macedonia_thrace=null;
           
           

            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 76) {
                    continue;
                }
                if (lineNumber > 83) {
                    break;
                }
                if (lineNumber == 76) {
                    total_da = data[2];
                } else if (lineNumber == 77) {
                    decentralized_administration_of_attica = data[3];
                } else if (lineNumber == 78) {
                    decentralized_administration_of_thessaly_central_greece = data[4];
                } else if (lineNumber == 79) {
                    decentralized_administration_of_epirus_western_macedonia = data[4];
                } else if (lineNumber == 80) {
                    decentralized_administration_of_peloponnese_western_greece_and_ionian = data[4];
                } else if (lineNumber == 81) {
                    decentralized_administration_of_aegean = data[3];
                } else if (lineNumber == 82) {
                    decentralized_administration_of_crete = data[3];
                } else if (lineNumber == 83) {
                    decentralized_administration_of_macedonia_thrace = data[3];
                }


            }
            statement.setBigDecimal(1, parseMoney(total_da));
            statement.setBigDecimal(2, parseMoney(decentralized_administration_of_attica));
            statement.setBigDecimal(3, parseMoney(decentralized_administration_of_thessaly_central_greece));
            statement.setBigDecimal(4, parseMoney(decentralized_administration_of_epirus_western_macedonia));
            statement.setBigDecimal(5, parseMoney(decentralized_administration_of_peloponnese_western_greece_and_ionian));
            statement.setBigDecimal(6, parseMoney(decentralized_administration_of_aegean));
            statement.setBigDecimal(7, parseMoney(decentralized_administration_of_crete));
            statement.setBigDecimal(8, parseMoney(decentralized_administration_of_macedonia_thrace));


                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    void insertBudgetSummary2024() throws Exception {
String sql = "INSERT INTO budget_summary_2024(budget_result,total_revenue,total_expenses,total_ministries,total_da) VALUES(?, ?, ?, ?, ?)";
        
        try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2024.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2024.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;

            String budget_result=null;
            String total_revenue=null;
            String total_expenses=null;
            String total_ministries=null;
            String total_da=null;

            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber == 41) {
                    budget_result=data[2];
                } else if (lineNumber ==13) {
                    total_revenue=data[3];
                } else if (lineNumber == 26) {
                    total_expenses=data[3];
                } else if (lineNumber ==55) {
                    total_ministries=data[4];
                } else if (lineNumber ==76 ) {
                    total_da=data[2];
                }
                    
                

            }
            statement.setBigDecimal(1, parseMoney(budget_result));
            statement.setBigDecimal(2, parseMoney(total_revenue));
            statement.setBigDecimal(3, parseMoney(total_expenses));
            statement.setBigDecimal(4, parseMoney(total_ministries));
            statement.setBigDecimal(5, parseMoney(total_da));
           

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ------------------ 2023 ------------------ //

    static void insertRevenue2023() throws Exception {
String sql = "INSERT INTO revenue_2023(total_revenue,taxes,social_contributions,transfers,sales_of_goods_and_services,other_current_revenue,fixed_assets,debt_securities,equity_securities_and_fund_shares,currency_and_deposit_liabilities,debt_securities_liabilities,loans_liabilities,financial_derivatives) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
         try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            

             File csvFile = new File("proipologismos2023.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2023.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
             String total_revenue = null;
                String taxes = null;
                String social_contributions = null;
                String transfers = null;
                String sales_of_goods_and_services = null;
                String other_current_revenue = null;
                String fixed_assets = null;
                String debt_securities = null;
                String equity_securities_and_fund_shares = null;
                String currency_and_deposit_liabilities = null;
                String debt_securities_liabilities = null;
                String loans = null;
                String financial_derivatives = null;
            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 10) {
                    continue;
                }
                if (lineNumber > 22) {
                    break;
                }
               
                if (lineNumber == 10) {
                    total_revenue = data[3];
                } else if (lineNumber == 11) {
                    taxes = data[3];
                } else if (lineNumber == 12) {
                    social_contributions = data[3];
                } else if (lineNumber == 13) {
                    transfers = data[3];
                } else if (lineNumber == 14) {
                    sales_of_goods_and_services = data[3];
                } else if (lineNumber == 15) {
                    other_current_revenue = data[3];
                } else if (lineNumber == 16) {
                    fixed_assets = data[3];
                } else if (lineNumber == 17) {
                    debt_securities = data[3];
                } else if (lineNumber == 18) {
                    equity_securities_and_fund_shares = data[3];
                } else if (lineNumber == 19) {
                    currency_and_deposit_liabilities = data[3];
                } else if (lineNumber == 20) {
                    debt_securities_liabilities = data[3];
                } else if (lineNumber == 21) {
                    loans = data[3];
                } else if (lineNumber == 22) {
                    financial_derivatives = data[3];
}



            }
             statement.setBigDecimal(1,  parseMoney(total_revenue));
                    statement.setBigDecimal(2,  parseMoney(taxes));
                    statement.setBigDecimal(3,  parseMoney(social_contributions));
                    statement.setBigDecimal(4,  parseMoney(transfers));
                    statement.setBigDecimal(5,  parseMoney(sales_of_goods_and_services));
                    statement.setBigDecimal(6,  parseMoney(other_current_revenue));
                    statement.setBigDecimal(7,  parseMoney(fixed_assets));
                    statement.setBigDecimal(8,  parseMoney(debt_securities));
                    statement.setBigDecimal(9,  parseMoney(equity_securities_and_fund_shares));
                    statement.setBigDecimal(10, parseMoney(currency_and_deposit_liabilities));
                    statement.setBigDecimal(11, parseMoney(debt_securities_liabilities));
                    statement.setBigDecimal(12, parseMoney(loans));
                    statement.setBigDecimal(13, parseMoney(financial_derivatives));
                    

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    static void insertExpenses2023() throws Exception {
String sql = "INSERT INTO expenses_2023(total_expenses,employee_benefits,social_benefits,transfers,purchases_of_goods_and_services,subsidies,interest,other_expenditures,appropriations,fixed_assets,valuables,loans,equity_securities_and_fund_shares,debt_securities_liabilities,loans_liabilities) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        

        try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2023.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2023.csv στον τρέχοντα φάκελο");
                return;
            }

            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
            String total_expenses=null;
            String employee_benefits=null;
            String social_benefits=null;
            String transfers=null;
            String purchases_of_goods_and_services=null;
            String subsidies=null;
            String interest=null;
            String other_expenditures=null;
            String appropriations=null;
            String fixed_assets=null;
            String valuables=null;
            String loans=null;
            String equity_securities_and_fund_shares=null;
            String debt_securities_liabilities=null;
            String loans_liabilities=null;


            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 23) {
                    continue;
                }
                if (lineNumber > 37) {
                    break;
                }
               
                if (lineNumber == 23) {
                    total_expenses = data[3];
                } else if (lineNumber == 24) {
                    employee_benefits = data[3];
                } else if (lineNumber == 25) {
                    social_benefits = data[3];
                } else if (lineNumber == 26) {
                    transfers = data[3];
                } else if (lineNumber == 27) {
                    purchases_of_goods_and_services = data[3];
                } else if (lineNumber == 28) {
                    subsidies = data[3];
                } else if (lineNumber == 29) {
                    interest = data[3];
                } else if (lineNumber == 30) {
                    other_expenditures = data[3];
                } else if (lineNumber == 31) {
                    appropriations = data[3];
                } else if (lineNumber == 32) {
                    fixed_assets = data[3];
                } else if (lineNumber == 33) {
                    valuables = data[3];
                } else if (lineNumber == 34) {
                    loans = data[3];
                } else if (lineNumber == 35) {
                    equity_securities_and_fund_shares = data[3];
                } else if (lineNumber == 36) {
                    debt_securities_liabilities = data[3];
                } else if (lineNumber == 37) {
                    loans_liabilities = data[3];
                }

            }
             statement.setBigDecimal(1,  parseMoney(total_expenses));
                    statement.setBigDecimal(2,  parseMoney(employee_benefits));
                    statement.setBigDecimal(3,  parseMoney(social_benefits));
                    statement.setBigDecimal(4,  parseMoney(transfers));
                    statement.setBigDecimal(5,  parseMoney(purchases_of_goods_and_services));
                    statement.setBigDecimal(6,  parseMoney(subsidies));
                    statement.setBigDecimal(7,  parseMoney(interest));
                    statement.setBigDecimal(8,  parseMoney(other_expenditures));
                    statement.setBigDecimal(9,  parseMoney(appropriations));
                    statement.setBigDecimal(10, parseMoney(fixed_assets));
                    statement.setBigDecimal(11, parseMoney(valuables));
                    statement.setBigDecimal(12, parseMoney(loans));
                    statement.setBigDecimal(13, parseMoney(equity_securities_and_fund_shares));
                    statement.setBigDecimal(14, parseMoney(debt_securities_liabilities));
                    statement.setBigDecimal(15, parseMoney(loans_liabilities));

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 
    static void insertMinistries2023() throws Exception {
String sql = "INSERT INTO ministries_2023(total_ministries,presidency_of_the_republic,hellenic_parliament,presidency_of_the_government,ministry_of_interior,ministry_of_foreign_affairs,ministry_of_national_defence,ministry_of_health,ministry_of_justice,ministry_of_education_religious_affairs_and_sports,ministry_of_culture,ministry_of_national_economy_and_finance,ministry_of_agricultural_development_and_food,ministry_of_environment_and_energy,ministry_of_labor_and_social_security,ministry_of_development,ministry_of_infrastructure_and_transport,ministry_of_maritime_affairs_and_insular_policy,ministry_of_tourism,ministry_of_digital_governance,ministry_of_migration_and_asylum,ministry_of_citizen_protection,ministry_of_climate_crisis_and_civil_protection) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2023.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2023.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;
            String total_ministries=null;
            String presidency_of_the_republic=null;
            String hellenic_parliament=null;
            String presidency_of_the_government=null;
            String ministry_of_interior=null;
            String ministry_of_foreign_affairs=null;
            String ministry_of_national_defence=null;
            String ministry_of_health=null;
            String ministry_of_justice=null;
            String ministry_of_education_religious_affairs_and_sports=null;
            String ministry_of_culture=null;
            String ministry_of_national_economy_and_finance=null;
            String ministry_of_agricultural_development_and_food=null;
            String ministry_of_environment_and_energy=null;
            String ministry_of_labor_and_social_security=null;
            String ministry_of_development=null;
            String ministry_of_infrastructure_and_transport=null;
            String ministry_of_maritime_affairs_and_insular_policy=null;
            String ministry_of_tourism=null;
            String ministry_of_digital_governance=null;
            String ministry_of_migration_and_asylum=null;
            String ministry_of_citizen_protection=null;
            String ministry_of_climate_crisis_and_civil_protection=null;
            



            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 49) {
                    continue;
                }
                if (lineNumber > 71) {
                    break;
                }
               String value = (data.length > 5) ? data[5] : "0";

                if (lineNumber == 49) {
                    presidency_of_the_republic = value;
                } else if (lineNumber == 50) {
                    hellenic_parliament = value;
                } else if (lineNumber == 51) {
                    presidency_of_the_government = value;
                } else if (lineNumber == 52) {
                    total_ministries = data[4];
                } else if (lineNumber == 53) {
                    ministry_of_interior = value;
                } else if (lineNumber == 54) {
                    ministry_of_foreign_affairs = value;
                } else if (lineNumber == 55) {
                    ministry_of_national_defence = data[5];
                } else if (lineNumber == 56) {
                    ministry_of_health = data[5];
                } else if (lineNumber == 57) {
                    ministry_of_justice = data[5];
                } else if (lineNumber == 58) {
                    ministry_of_education_religious_affairs_and_sports = value;
                } else if (lineNumber == 59) {
                    ministry_of_culture = data[5];
                } else if (lineNumber == 60) {
                    ministry_of_national_economy_and_finance = data[5];
                } else if (lineNumber == 61) {
                    ministry_of_agricultural_development_and_food = data[5];
                } else if (lineNumber == 62) {
                    ministry_of_environment_and_energy = data[5];
                } else if (lineNumber == 63) {
                    ministry_of_labor_and_social_security = data[5];
                } else if (lineNumber == 64) {
                    ministry_of_development = data[5];
                } else if (lineNumber == 65) {
                    ministry_of_infrastructure_and_transport = data[5];
                } else if (lineNumber == 66) {
                    ministry_of_maritime_affairs_and_insular_policy = data[5];
                } else if (lineNumber == 67) {
                    ministry_of_tourism = data[5];
                } else if (lineNumber == 68) {
                    ministry_of_digital_governance = data[5];
                } else if (lineNumber == 69) {
                    ministry_of_migration_and_asylum = data[5];
                } else if (lineNumber == 70) {
                    ministry_of_citizen_protection = value;
                } else if (lineNumber == 71) {
                    ministry_of_climate_crisis_and_civil_protection = data[5];
                }



            }
            statement.setBigDecimal(1,  parseMoney(total_ministries));
            statement.setBigDecimal(2,  parseMoney(presidency_of_the_republic));
            statement.setBigDecimal(3,  parseMoney(hellenic_parliament));
            statement.setBigDecimal(4,  parseMoney(presidency_of_the_government));
            statement.setBigDecimal(5,  parseMoney(ministry_of_interior));
            statement.setBigDecimal(6,  parseMoney(ministry_of_foreign_affairs));
            statement.setBigDecimal(7,  parseMoney(ministry_of_national_defence));
            statement.setBigDecimal(8,  parseMoney(ministry_of_health));
            statement.setBigDecimal(9,  parseMoney(ministry_of_justice));
            statement.setBigDecimal(10, parseMoney(ministry_of_education_religious_affairs_and_sports));
            statement.setBigDecimal(11, parseMoney(ministry_of_culture));
            statement.setBigDecimal(12, parseMoney(ministry_of_national_economy_and_finance));
            statement.setBigDecimal(13, parseMoney(ministry_of_agricultural_development_and_food));
            statement.setBigDecimal(14, parseMoney(ministry_of_environment_and_energy));
            statement.setBigDecimal(15, parseMoney(ministry_of_labor_and_social_security));
            statement.setBigDecimal(16, parseMoney(ministry_of_development));
            statement.setBigDecimal(17, parseMoney(ministry_of_infrastructure_and_transport));
            statement.setBigDecimal(18, parseMoney(ministry_of_maritime_affairs_and_insular_policy));
            statement.setBigDecimal(19, parseMoney(ministry_of_tourism));
            statement.setBigDecimal(20, parseMoney(ministry_of_digital_governance));
            statement.setBigDecimal(21, parseMoney(ministry_of_migration_and_asylum));
            statement.setBigDecimal(22, parseMoney(ministry_of_citizen_protection));
            statement.setBigDecimal(23, parseMoney(ministry_of_climate_crisis_and_civil_protection));

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void insertDecentralizedAdministrations2023() throws Exception {
String sql = "INSERT INTO decentralized_administrations_2023(total_da,decentralized_administration_of_attica,decentralized_administration_of_thessaly_central_greece,decentralized_administration_of_epirus_western_macedonia,decentralized_administration_of_peloponnese_western_greece_and_ionian,decentralized_administration_of_aegean,decentralized_administration_of_crete,decentralized_administration_of_macedonia_thrace) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";


         try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2023.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2023.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;

            String total_da=null;
            String decentralized_administration_of_attica=null;
            String decentralized_administration_of_thessaly_central_greece=null;
            String decentralized_administration_of_epirus_western_macedonia=null;
            String decentralized_administration_of_peloponnese_western_greece_and_ionian=null;
            String decentralized_administration_of_aegean=null;
            String decentralized_administration_of_crete=null;
            String decentralized_administration_of_macedonia_thrace=null;
           
           

            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber < 72) {
                    continue;
                }
                if (lineNumber > 79) {
                    break;
                }
                if (lineNumber == 72) {
                    total_da = data[2];
                } else if (lineNumber == 73) {
                    decentralized_administration_of_attica = data[3];
                } else if (lineNumber == 74) {
                    decentralized_administration_of_thessaly_central_greece = data[5];
                } else if (lineNumber == 75) {
                    decentralized_administration_of_epirus_western_macedonia = data[5];
                } else if (lineNumber == 76) {
                    decentralized_administration_of_peloponnese_western_greece_and_ionian = data[5];
                } else if (lineNumber == 77) {
                    decentralized_administration_of_aegean = data[3];
                } else if (lineNumber == 78) {
                    decentralized_administration_of_crete = data[3];
                } else if (lineNumber == 79) {
                    decentralized_administration_of_macedonia_thrace = data[3];
                }


            }
            statement.setBigDecimal(1, parseMoney(total_da));
            statement.setBigDecimal(2, parseMoney(decentralized_administration_of_attica));
            statement.setBigDecimal(3, parseMoney(decentralized_administration_of_thessaly_central_greece));
            statement.setBigDecimal(4, parseMoney(decentralized_administration_of_epirus_western_macedonia));
            statement.setBigDecimal(5, parseMoney(decentralized_administration_of_peloponnese_western_greece_and_ionian));
            statement.setBigDecimal(6, parseMoney(decentralized_administration_of_aegean));
            statement.setBigDecimal(7, parseMoney(decentralized_administration_of_crete));
            statement.setBigDecimal(8, parseMoney(decentralized_administration_of_macedonia_thrace));


                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }

    static void insertBudgetSummary2023() throws Exception {
String sql = "INSERT INTO budget_summary_2023(budget_result,total_revenue,total_expenses,total_ministries,total_da) VALUES(?, ?, ?, ?, ?)";

         try {
            Connection connection = DatabaseConnection.getConnection();

            

            PreparedStatement statement = connection.prepareStatement(sql);

             File csvFile = new File("proipologismos2023.csv");
            if (!csvFile.exists()) {
                System.out.println("Δεν βρέθηκε το αρχείο proipologismos2023.csv στον τρέχοντα φάκελο");
                return;
            }


            BufferedReader lineread = new BufferedReader(new FileReader(csvFile));

            String  linetext= null;
            
            lineread.readLine();
            int lineNumber=1;

            String budget_result=null;
            String total_revenue=null;
            String total_expenses=null;
            String total_ministries=null;
            String total_da=null;

            while ((linetext=lineread.readLine())!=null) {
                lineNumber++;
                String [] data =linetext.split(",");

                if (lineNumber == 38) {
                    budget_result=data[2];
                } else if (lineNumber ==10) {
                    total_revenue=data[3];
                } else if (lineNumber == 23) {
                    total_expenses=data[3];
                } else if (lineNumber ==52) {
                    total_ministries=data[4];
                } else if (lineNumber ==72 ) {
                    total_da=data[2];
                }
                    
                

            }
            statement.setBigDecimal(1, parseMoney(budget_result));
            statement.setBigDecimal(2, parseMoney(total_revenue));
            statement.setBigDecimal(3, parseMoney(total_expenses));
            statement.setBigDecimal(4, parseMoney(total_ministries));
            statement.setBigDecimal(5, parseMoney(total_da));
           

                statement.addBatch();
                statement.executeBatch();
               

             lineread.close(); 
                connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



public static void updateRevenue(
        int year,
        int collum,
        String change,
        String newValue
        
    
) {

if (year==2025||year==2026){ 

    Set<String> allowedColumns = Set.of(
        "taxes",
        "social_contributions",
        "transfers",
        "sales_of_goods_and_services",
        "other_current_revenue",
        "fixed_assets",
        "debt_securities",
        "loans",
        "equity_securities_and_fund_shares",
        "currency_and_deposit_liabilities",
        "debt_securities_liabilities",
        "loans_liabilities",
        "financial_derivatives"
    );

    if (!allowedColumns.contains(change)) {
        throw new IllegalArgumentException("Μη έγκυρη στήλη");
    }

    String sql = "UPDATE revenue_" + year +
                 " SET " + change + " = ? WHERE total_revenue = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setBigDecimal(collum, parseMoney(newValue));
        

        pstmt.executeUpdate();

    } catch (SQLException e) {
        System.out.println("Σφάλμα UPDATE: " + e.getMessage());
    }
}else {
    Set<String> allowedColumns = Set.of(
        "taxes",
        "social_contributions",
        "transfers",
        "sales_of_goods_and_services",
        "other_current_revenue",
        "fixed_assets",
        "debt_securities",
        "equity_securities_and_fund_shares",
        "currency_and_deposit_liabilities",
        "debt_securities_liabilities",
        "loans_liabilities",
        "financial_derivatives"
    );

    if (!allowedColumns.contains(change)) {
        throw new IllegalArgumentException("Μη έγκυρη στήλη");
    }

    String sql = "UPDATE revenue_" + year +
                 " SET " + change + " = ? WHERE total_revenue = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setBigDecimal(collum, parseMoney(newValue));
        

        pstmt.executeUpdate();

    } catch (SQLException e) {
        System.out.println("Σφάλμα UPDATE: " + e.getMessage());
    }
}

}



}

