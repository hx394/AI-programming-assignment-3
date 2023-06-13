import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Collections;

import java.util.Scanner;

public class back_end {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		if(args.length<=0) {
			System.out.println("Please execute with the file name.");
			return;
		}
		
		File file1=new File(args[0]);
		Scanner in=new Scanner(file1);
		
		String firstLine=in.nextLine();
		ArrayList<String> numbers=new ArrayList<String>();
		ArrayList<node> nodeList=new ArrayList<node>();
		String output="";
		
		//check the first line to know if there is no solution or there is solution
		if(firstLine.equals("0")) {
			output="NO SOLUTION";
			System.out.print(output);
			return;
		}else {
			String[] fl=firstLine.split(" ");
			if(fl[1].equals("T")) {
				numbers.add(fl[0]);
			}
			//passZero check if pass zero
			boolean passZero=false;
			while(in.hasNextLine()) {
				String nextLine=in.nextLine();
				if(nextLine.equals("0")) {
					passZero=true;
				}
				if(passZero==false) {
					String[] nl=nextLine.split(" ");
					if(nl[1].equals("T")) {
						numbers.add(nl[0]);
					}
				}else {
					if(!nextLine.equals("0")&& !nextLine.equals("")) {
						String [] nl=nextLine.split(" ");
						if(numbers.contains(nl[0])) {
							nodeList.add(new node(nl[1],Integer.parseInt(nl[2])));
						}
					}
				}
			}
			//sort the node list
			Collections.sort(nodeList);
			for(int i=0;i<nodeList.size();i++) {
				output=output+nodeList.get(i).letter;
			}
		}
		//write to file
		File file2=new File("BackEndOutput.txt");
		file2.createNewFile();
		FileWriter writer=new FileWriter(file2);
		writer.write(output);
		
		writer.close();

		
		in.close();
	}

}
//each node is a combination of letter and position
class node implements Comparable{
	String letter;
	int position;
	node(String letter, int position){
		this.letter=letter;
		this.position=position;
	}
	@Override
	public int compareTo(Object other) {
		int otherPosition=((node)other).position;
		return this.position-otherPosition;
	}

}
