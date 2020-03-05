package traasTest;



import java.util.Arrays;
import java.util.List;


import de.tudresden.sumo.cmd.Inductionloop;
import de.tudresden.sumo.cmd.Junction;
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.cmd.Lanearea;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.cmd.Vehicletype;
import de.tudresden.sumo.subscription.SumoDomain;
import de.tudresden.sumo.util.SumoCommand;
import de.tudresden.ws.container.SumoTLSProgram;
import de.tudresden.ws.container.SumoVehicleData;
import de.tudresden.ws.container.SumoVehicleData.VehicleData;
import it.polito.appeal.traci.SumoTraciConnection;



public class Main {


	public static void main(String[] args) {
		Double max =0.0;
		Double current,currentPos;
		double[] mylist = new double[300];
		double[] pos = new double[7];
		Arrays.fill(mylist, 0.0);
		Arrays.fill(pos, 0);
		float somme = 0;
        String sumo_bin = "sumo-gui";
        String config_file = "C:\\Users\\Ayoub\\Documents\\ILISI2\\ProjetIntegration\\traffic\\traasTest2\\src\\traasTest\\data\\config.sumocfg";
        double step_length = 0.1;

        if (args.length > 0) {
            sumo_bin = args[0];
        }
        if (args.length > 1) {
            config_file = args[1];
        }
        Arrays[] n = new Arrays[200];
        try {
            SumoTraciConnection conn = new SumoTraciConnection(sumo_bin, config_file);
            conn.addOption("step-length", step_length + "");
            conn.addOption("start", "true"); //start sumo immediately

            //start Traci Server
            conn.runServer();
            conn.setOrder(1);
            
            
            for (int i = 0; i < 3600; i++) {
            	
                conn.do_timestep();
                conn.do_job_set(Vehicle.addFull("v" + i, "r1", "car", "now", "0", "0", "max", "current", "max", "current", "", "", "", 0, 0));
                double timeSeconds = (double)conn.do_job_get(Simulation.getTime());
                int tlsPhase = (int)conn.do_job_get(Trafficlight.getPhase("gneJ1"));
                String tlsPhaseName = (String)conn.do_job_get(Trafficlight.getPhaseName("gneJ1"));
               // System.out.println(String.format("Simulaion time : "+timeSeconds));
                
                if(timeSeconds == 168.6) 
                	break;
          
                List<String> supplierNames1 = (List<String>) conn.do_job_get(Vehicle.getIDList());
               
                for (String d : supplierNames1) {
                	
                		if((conn.do_job_get(Vehicle.getTypeID(d)).equals("vType_0")))
                		{
                			currentPos = Double.parseDouble(conn.do_job_get(Vehicle.getPosition(d)).toString().split(",")[0]);
                			//System.out.println("TRAM : "+d+"   "+conn.do_job_get(Vehicle.getPosition(d)).toString().split(",")[0]);
                			if(currentPos > 460.0)
                			{
                				//System.out.println(d+ "Arrived !!");
                				//conn.do_job_set(Trafficlight.setRedYellowGreenState("gneJ1", "yyyyrrr"));
                				conn.do_job_set(Trafficlight.setRedYellowGreenState("gneJ1", "rrrrGGG"));
                			}
                			if(currentPos > 540)
                			{
                				//System.out.println(d+ "Out !!");
                				conn.do_job_set(Trafficlight.setRedYellowGreenState("gneJ1", "GGGGrrr"));
                			}
                		}
                	
                	if((conn.do_job_get(Vehicle.getTypeID(d)).equals("car"))) {
                		
                    	int ind = Integer.parseInt(d.substring(1));
                    	current = (double)conn.do_job_get(Vehicle.getWaitingTime(d));
                    	if(current > mylist[ind])
                    		mylist[ind] = current;
                    	
//                    	if(current == 0 && mylist[ind]!=0);
//                    		System.out.println("veh"+ind+" : "+mylist[ind]);
                	}
                }
            }
            for(int i = 0;i<140;i++)
            {
            	somme+=mylist[i];
            	System.out.println("veh"+i+" : "+mylist[i]);
            }
            System.out.println("The average time : "+somme/140);
            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

	}

}
