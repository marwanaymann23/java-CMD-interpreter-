import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;


class Parser {
    String commandName;
    String[] args;

	//This method will divide the input into commandName and args
	//where "input" is the string command entered by the user
    public boolean parse(String input){

        boolean twoWordsCommand = false;

        //read the input as a line and store the words in an array
        String splitInput[] = input.split(" ");

        commandName = splitInput[0];
        if (commandName.equalsIgnoreCase("exit")) {
            //if the command is "exit" return false to terminate
            return false;
        }
        // && 
        if (splitInput.length > 1 && splitInput[1].startsWith("-")) {
            //the second word is also a part of the command so we concatenate it to the string
            //containing the command and set the flag to true
            commandName += " " + splitInput[1];
            twoWordsCommand = true;
        }
        if (twoWordsCommand) {
            //the first two indices are the command so the first argument is at index[2]
            args = new String[splitInput.length - 2];
            for(int i = 2; i < splitInput.length; i++) {
                args[i - 2] = splitInput[i];
            }
        }
        else {
            //the command is the first index only so the first argument is at index[1]
            args = new String[splitInput.length - 1];
            for(int i = 1; i < splitInput.length; i++) {
                args[i - 1] = splitInput[i];
            }
        }
        return true;
    }

    public String getCommandName(){
        return commandName;
    }

    public String[] getArgs(){
        return args;
    }
}

public class Terminal {
    Parser parser = new Parser();


    //This method takes one argument and prints it
    public void echo(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.print(args[i] + " ");
        }
        System.out.println("");
    }

    //This method prints the current path.
    public String pwd(){
        String path  = "Current Directory:- " + System.getProperty("user.dir");
        return path;
    }

    //This method covers all the directory cases
    //1- Changes the current path to the path of your home directory.
    //2- Changes the current directory to the previous directory.
    //3- Changes the current path to a specific path (Entered by the user).
    public void cd(String[] args){
        
        // no arguments case
        if(args.length == 0) {
            System.setProperty("user.dir", System.getProperty("user.home"));
            System.out.println(System.getProperty("user.home"));
        }
        
        // arguments case
        // cd ..
        else if(args[0].equals("..")) {
            String path = System.getProperty("user.dir");
            path = path.substring(0, path.lastIndexOf("\\"));
            System.setProperty("user.dir", path);
            System.out.println(System.getProperty("user.dir"));
        }
        // cd + path
        else {
        	String path = args[0];
            // loop to take the path from the args array in one string
            for(int i = 1; i< args.length; i++){
                path += " "  + args[i];
            }
            
           //check if the path exists
            if(Files.exists(Paths.get(path))) {
            	System.setProperty("user.dir", path);
                System.out.println(System.getProperty("user.dir"));
            }
          //check if the input is a valid relative path
            else if (!path.contains(":") && Files.exists(Paths.get(System.getProperty("user.dir") + "\\" +  path))) {	
            	System.setProperty("user.dir", System.getProperty("user.dir") + "\\" +  path);
                System.out.println(System.getProperty("user.dir"));
            }
            else {
            	System.out.println("Error: Path doesn't exist!");
            }
        }
    }
    

    // This method lists contents of a directory
    public void ls() {
        File directory = new File(System.getProperty("user.dir"));
        File[] files = directory.listFiles();
        String[] filesList = new String[files.length];
        int i = 0;
        for (File file : files) {
            if (!file.isHidden()) {
               if (file.isDirectory()) {
                  filesList[i] = "FOLDER\t" + file.getName();
                 
               } else {
                   filesList[i] = "FILE\t" + file.getName();
               }
               i++;
            }
         }
        if(parser.getCommandName().equals("ls -r")) {
            for(int j = files.length - 1; j >= 0 ; j--) {
                System.out.println(filesList[j] );
            }
        }else if(parser.getCommandName().equals("ls")) {
            for(int j = 0; j < files.length; j++) {
                System.out.println(filesList[j]);
            }
        }       
    }
    
    // This method creates directories
    public void mkdir(String[] args) {
    	
    	for(int i = 0; i < args.length; i++) {
    		
    		// full path condition F:\
    		if(args[i].contains(":\\")) {
    			
    			String path = args[i].substring(0, args[i].lastIndexOf('\\'));
    			String directoryName = args[i].substring(args[i].lastIndexOf('\\') + 1);
    			
    			// Checks if the path exist or not
    			if(Files.exists(Paths.get(path))) {
    				
    				 // Checks if the directory exist or not
    				if(!Files.exists(Paths.get(path + "\\" + directoryName))) {
    					new File(path + "\\" + directoryName).mkdirs();
    				}
    				else {
    					System.out.println("Error: Directory already exsits");
    				}
    				
    			}
    			else {
    				System.out.println("Error: Path doesn't exist");
    			}
    		}
    		
    		
    		// Short path condition path\directoryName
    		else if(!args[i].contains(":") && args[i].contains("\\")) {
    			
    			String path = args[i].substring(0, args[i].lastIndexOf('\\'));
    			String directoryName = args[i].substring(args[i].lastIndexOf('\\') + 1);
    			
    			// Checks if the path exist or not
    			if(Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + path))) {
    				
    				// Checks if the directory exist or not
    				if(!Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + path + "\\" + directoryName))) {
    					new File(System.getProperty("user.dir") + "\\" + path + "\\" + directoryName).mkdirs();
    				}
    				else {
    					System.out.println("Error: Directory already exsits");
    				}
    				
    			}
    			else {
    				System.out.println("Error: Path doesn't exist");
    			}
    		}
    		
    		
    		// directory name condition
    		else {
    			if(!Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + args[i]))) {
					new File(System.getProperty("user.dir") + "\\" + args[i]).mkdirs();
				}
				else {
					System.out.println("Error: Directory already exsits");
				}
    		}
    	}
    }
    
    // This method deletes directories
    public void rmdir(String[] args) {
    	
    	if(args[0].equals("*")) {
    		File directory = new File(System.getProperty("user.dir"));
            File[] files = directory.listFiles();
            
            for (File file : files) {
            	
                if (!file.isHidden()) {
                	
                   if (file.isDirectory()) {
                	   if(file.delete()){  
                	   System.out.println(file.getName() + " deleted");    
                	   }
                	   else{  
                	   System.out.println(file.getName() + " is not empty");  
                	   }
                   } 
                  
                }
             }
    	}
    	// Path condition
    	else {    		
    		String rmdirPath = "";
    		
    		// Full path condition
    		if(args[0].contains(":\\")) {
    			    			
    			// Checks if the path exist or not
    			if(Files.exists(Paths.get(args[0]))) {
    				rmdirPath = args[0];
    			}
    			else {
    				System.out.println("Error: Path doesn't exist");
    			}
    		}
    		
    		// Short path condition path\directoryName
    		else if(!args[0].contains(":") && args[0].contains("\\")) {
    						
    			// Checks if the path exist or not
    			if(Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + args[0]))) {
    				rmdirPath = System.getProperty("user.dir") + "\\" + args[0];
    			}
    			else {
    				System.out.println("Error: Path doesn't exist");
    			}
    		}
	
    		// directory name condition
    		else {
    			if(Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + args[0]))) {
    				rmdirPath = System.getProperty("user.dir") + "\\" + args[0];
				}
				else {
					System.out.println("Error: Path doesn't exist");
				}
    		}
    			
    		File directory = new File(rmdirPath);
            File[] files = directory.listFiles();
            for (File file : files) {            	
                if (!file.isHidden()) {                	
                   if (file.isDirectory()) {
                	   if(file.delete()){  
                	   System.out.println(file.getName() + "	 deleted");    
                	   }
                	   else{  
                	   System.out.println(file.getName() + "	 is not empty");  
                	   }
                   } 
                  
                }
             }
            
    	}	
    }
    
    //This method copies a file into another file
    public void cp(String[] args) {
    	if(args.length == 2) {
    		String path1 = "";
        	String path2 = "";
        	boolean filesExist = true;
        	
        	// Check if absolute path exists
        	if(Files.exists(Paths.get(args[0]))) {
        		path1 = args[0];
        	}
        	// Check if relative path exists
        	else if(!args[0].contains(":") && Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + args[0]))) {
        		path1 = System.getProperty("user.dir") + "\\" + args[0];
        	}
        	else {
        		filesExist = false;
        		System.out.println("Error: File doesn't exist");
        	}
        	
        	if (filesExist) {
        		// Check if absolute path exists
            	if(Files.exists(Paths.get(args[1]))) {
            		path2 = args[1];
            	}
            	// Check if relative path exists
            	else if(!args[1].contains(":") && Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + args[1]))) {
            		path2 = System.getProperty("user.dir") + "\\" + args[1];
            	}
            	else {
            		filesExist = false;
            		System.out.println("Error: File doesn't exist");
            	}
        	}
        	
        	if (filesExist) {
        		try {
					Scanner scanner = new Scanner(new File(path1));
					FileWriter writer = new FileWriter(new File(path2), true);
					String content = "";
					while(scanner.hasNextLine()) {
	        			content = content.concat(scanner.nextLine() + "\n");	
	        		}
					writer.write(content);
					scanner.close();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 		
        	} 	
    }
    	else {
		System.out.println("Error: Command not found or invalid parameters are entered!");
	}
    }
    
    //This method copies a directory's contents into another directory (calls the copyDir() method)
    public void cpr(String[] args) {
    	if(args.length == 2) {
    		String path1 = "";
        	String path2 = "";
        	boolean filesExist = true;
        	
        	// Check if absolute path exists
        	if(Files.exists(Paths.get(args[0]))) {
        		path1 = args[0];
        	}
        	// Check if relative path exists
        	else if(!args[0].contains(":") && Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + args[0]))) {
        		path1 = System.getProperty("user.dir") + "\\" + args[0];
        	}
        	else {
        		filesExist = false;
        		System.out.println("Error: File doesn't exist");
        	}
        	
        	if (filesExist) {
        		// Check if absolute path exists
            	if(Files.exists(Paths.get(args[1]))) {
            		path2 = args[1];
            	}
            	// Check if relative path exists
            	else if(!args[1].contains(":") && Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + args[1]))) {
            		path2 = System.getProperty("user.dir") + "\\" + args[1];
            	}
            	else {
            		filesExist = false;
            		System.out.println("Error: File doesn't exist");
            	}
        	}
        	
        	if (filesExist) {
        		copyDir(path1, path2);
        	}
    }
    	else {
    		System.out.println("Error: Command not found or invalid parameters are entered!");
    	}
    }
    
    //This method is called by cpr() method 
    //cpr() method checks if the paths entered are valid before calling this method to do the actual copying
    //This method calls itself recursively
    public void copyDir(String path1, String path2) {
    	File source = new File(path1);
    	
    	//Create a directory with the same name in the destination directory
    	File newDirectory = new File(path2 + "\\" + path1.substring(path1.lastIndexOf("\\") + 1));
    	newDirectory.mkdirs();
    	
    	File files[] = source.listFiles();
    	
    	//Loop on the files and folders in the source directory
    	for (File file : files) {
    		//If the path represents a file, copy it to the destination directory
    		if(file.isFile()) {
    			File newFile = new File(path2 + "\\" + path1.substring(path1.lastIndexOf("\\") + 1) + "\\" + file.getName());
    			try {
					newFile.createNewFile();
					String[] copyFile = new String[2];
					copyFile[0] = path1 + "\\" + file.getName();
					copyFile[1] =  path2 + "\\" + path1.substring(path1.lastIndexOf("\\") + 1) + "\\" + file.getName();
					cp(copyFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    		//If the path represents a directory, call the method recursively
    		else {
    			copyDir(path1 + "\\" + file.getName(), path2 + "\\" + path1.substring(path1.lastIndexOf("\\") + 1));
    		}
    	}
    }
    
    //This method creates files
    public void touch(String[] args) {
    	String path = args[0];
		for(int i = 1; i < args.length; i++) {
			path += " "  + args[i];
		}
		String createFile = path.substring(path.lastIndexOf("\\") + 1, path.length());
		path = path.substring(0, path.lastIndexOf("\\"));
		//check if the path exists
        if(Files.exists(Paths.get(path))) {
        	File file = new File(path + "\\" + createFile);
      		try {
				if (file.createNewFile()) {
					System.out.println("File created: " + file.getName());
				} else {
					System.out.println("Error: File already exists!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
      //check if the input is a valid relative path
        else if (Files.exists(Paths.get(System.getProperty("user.dir") + "\\" +  path))) {	
        	//check if the file doesn't already exist
        	if(!Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + path + "\\" + createFile))) {
            	File file = new File(System.getProperty("user.dir") + "\\" + path + "\\" + createFile);
          		try {
    				if (file.createNewFile()) {
    					System.out.println("File created: " + file.getName());
    				} else {
    					System.out.println("Error: File already exists!");
    				}
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
			}
        }
        else {
        	System.out.println("Error: Path doesn't exist!");
        }
	}
    
    //This method deletes a file
    public void rm(String[] args) {

    	if(Files.exists(Paths.get(System.getProperty("user.dir") + "\\" +  args[0]))){
    		File file = new File(System.getProperty("user.dir") + "\\" +  args[0]);
    		file.delete();
    		System.out.println("File Deleted");
    	}
    	else {
    		System.out.println("Error: File doesn't exist!");
    	}
    }
    
    public void cat(String[] args) throws IOException {
    	
    	// One argument case
    	if(args.length == 1) {    		
    		// Checks if the path exist or not
    		if(Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + args[0]))) {
    			
    			      File file = new File(System.getProperty("user.dir") + "\\" + args[0]);
    			      Scanner reader = new Scanner(file);
    			      while (reader.hasNextLine()) {
    			        String dataLine = reader.nextLine();
    			        System.out.println(dataLine);
    			      }
    			      reader.close();   
    			  }
			}
    		    	
    	// Two argument case
    	else if(args.length == 2){
    		
    		// Checks if the two paths are existed or not
    		if(Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + args[0])) && 
    			Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + args[1]))) {
    			
    			
    				  // Copy from file1
    			      File file1 = new File(System.getProperty("user.dir") + "\\" + args[0]);
    			      // Paste in file2
    			      File file2 = new File(System.getProperty("user.dir") + "\\" + args[1]);
    			      
    			      // Scanner to read from file1
    			      Scanner reader = new Scanner(file1);
    			      
    			      // File Writer to file2
    			      FileWriter writer = new FileWriter(file2, true);
    			      BufferedWriter bufferedWriter = new BufferedWriter(writer);

    			      // loop to write on file2 line by line from file1
    			      while (reader.hasNextLine()) {
    			        String data = reader.nextLine();
    			        bufferedWriter.write("\n" + data);
    			      }
    			      bufferedWriter.close();
    			      reader.close();
    			      
    			      // loop to print file2
    			      Scanner reader2 = new Scanner(file2);
    			      while (reader2.hasNextLine()) {
      			        String dataLine = reader2.nextLine();
      			        System.out.println(dataLine);
      			      }
      			      reader2.close();
    			  }
    	}
    	else {
    		System.out.println("Invalid number of arguments, Please enter one argument or two!");
    	}
    }
    
    //This method will choose the suitable command method to be called
    public void chooseCommandAction() throws IOException{
        
        String command = parser.getCommandName();
        
        if (command.equals("echo")) {
            echo(parser.getArgs());
        }
        else if(command.equals("pwd")) {
            System.out.println(pwd());
        }
        else if(command.equals("cd")) {
            cd(parser.getArgs());
        }
        else if(command.equals("ls") || command.equals("ls -r")) {
            ls();
        }
        else if(command.equals("mkdir")) {
            mkdir(parser.getArgs());
        }
        else if(command.equals("rmdir")) {
        	rmdir(parser.getArgs());
        }
        else if(command.equals("touch")) {
            touch(parser.getArgs());
        }
        else if(command.equals("rm")) {
            rm(parser.getArgs());
        }
        else if(command.equals("cat")) {
            cat(parser.getArgs());
        }
        else if(command.equals("cp -r")) {
        	cpr(parser.getArgs());	
        }
        else if(command.equals("cp")) {
        	cp(parser.getArgs());	
        }
        else {
            System.out.println("Error: Command not found or invalid parameters are entered!");
        }
    }

    public static void main(String[] args) throws IOException{
    	
        Terminal terminal = new Terminal();
        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();

        while(terminal.parser.parse(input)) {
        	try {
        		terminal.chooseCommandAction();
			} catch (Exception e) {
				System.out.println("Error: Invalid argument");
			}          
            input = scanner.nextLine();
        }

        scanner.close();
    }
}