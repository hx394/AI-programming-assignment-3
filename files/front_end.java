import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class front_end {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if(args.length<=0) {
			System.out.println("Please execute with the file name.");
			return;
		}
		File file;
		file=new File(args[0]);
		Scanner in=new Scanner(file);
		
		String firstLine=in.nextLine();
		//vertices number
		int vnum=0;
		try {
			vnum=Integer.parseInt(firstLine);
		}catch(NumberFormatException ex){
			ex.printStackTrace();
		}
		
		//generate the key to be attached
		String key="";
		for(int i=0;i<vnum;i++) {
			for(int j=0;j<vnum;j++) {
				key=key+(i*vnum+j+1);
				key=key+" "+Character.toString((char)(i+65))+" "+(j+1)+"\n";
			}
		}
		
		//read in the lines
		boolean [][] relation=new boolean[vnum][vnum];
		while(in.hasNextLine()) {
			String [] newLine=in.nextLine().split(" ");
			int start=(int)(newLine[0].charAt(0)-65);
			int end=(int)(newLine[1].charAt(0)-65);
			relation[start][end]=true;
		}
		
		
		//generate clauses
		String clauses="";
		//every vertex is traversed at some time
		for(int i=0;i<vnum;i++) {
			for(int j=0;j<vnum;j++) {
				clauses=clauses+(i*vnum+j+1);
				if((j+1)!=vnum) {
					clauses+=" ";
				}
			}
			clauses+="\n";
		}
		//no pair of vertices are traversed at the same time
		for(int k=0;k<vnum;k++) {
			for(int i=0;i<vnum;i++) {
				for(int j=i+1;j<vnum;j++) {
					clauses=clauses+(-(i*vnum+k+1))+" "+(-(j*vnum+k+1))+"\n";
				}
			}
		}
		//you cannot go from UT to W(T+1) if there is no edge from U to W
		
		for(int i=0;i<vnum;i++) {
			for(int j=0;j<vnum;j++) {
				if(relation[i][j]==false && i!=j) {
					for(int k=1;k+1<=vnum;k++) {
						clauses=clauses+(-(i*vnum+k))+" "+(-(j*vnum+k+1))+"\n";
						
					}
				}
			}
		}
		//at every time there is a vertex
		for(int i=1;i<=vnum;i++) {
			for(int j=0;j<vnum;j++) {
				clauses=clauses+(j*vnum+i);
				if(j+1!=vnum) {
					clauses+=" ";
				}
			}
			clauses+="\n";
		}
		//no vertex is traversed more than once
		for(int i=0;i<vnum;i++) {
			for(int j=0;j<vnum;j++) {
				for(int k=j+1;k<vnum;k++) {
					int num1=-(i*vnum+j+1);
					int num2=-(i*vnum+k+1);
					clauses=clauses+num1+" "+num2+"\n";
				}
			}
		}
		String output=clauses+"0\n"+key;
		
		File file2=new File("FrontEndOutput.txt");
		file2.createNewFile();
		FileWriter writer=new FileWriter(file2);
		writer.write(output);
		
		writer.close();
		in.close();
	}

}
