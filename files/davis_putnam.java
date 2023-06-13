import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class davis_putnam {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		if(args.length<=0) {
			System.out.println("Please execute with the file name.");
			return;
		}
		
		File file1=new File(args[0]);
		Scanner in=new Scanner(file1);
		
		int max=0;
		ArrayList <ArrayList<Integer>> myArrayList=new ArrayList<ArrayList<Integer>>();
		
		//add the clauses and comment
		boolean comment=false;
		String comm="";
		while(in.hasNextLine()) {
			
			String newLine=in.nextLine();

			if(!newLine.equals("")&&!newLine.equals("0")&&comment==false) {
				String [] lineArray=newLine.split(" ");
				ArrayList<Integer> array=new ArrayList<Integer>();
				for(int i=0;i<lineArray.length;i++) {
					int number=Integer.parseInt(lineArray[i]);
					if(number>max) {
						max=number;
					}else if(-number>max) {
						max=-number;
					}
					array.add(number);
				}
				myArrayList.add(array);
			}else if(newLine.equals("0")&&comment==false) {
				comment=true;
			}
			
			if(comment==true) {
				comm=comm+newLine;
				if(in.hasNextLine()) {
					comm+="\n";
				}	
			}
		}
		
		int [] valueTable=new int[max];
		
		int [] solution=dp1(max,myArrayList,valueTable);
		
		String str="";
		if(solution==null) {
			str=comm;
		}else {
			for(int i=0;i<solution.length;i++) {
				str=str+(i+1)+" ";
				if(solution[i]==1) {
					str=str+"T\n";
				}else if(solution[i]==-1) {
					str=str+"F\n";
				}
			}
			str=str+comm;
		}
		
		File file2=new File("DavisPutnamOutput.txt");
		file2.createNewFile();
		FileWriter writer=new FileWriter(file2);
		writer.write(str);
		
		writer.close();
		
		in.close();
	}
	
	static int[] dp1(int max,ArrayList<ArrayList<Integer>> myArrayList,int[] valueTable){
		//loop as long as there are easy cases to cherry pick
		int counter=0;
		while(counter!=4) {
			counter=0;
			//base of the recursion: success or failure
			//success
			if(myArrayList.isEmpty()) {
				for(int i=0;i<max;i++) {
					if(valueTable[i]==0) {
						valueTable[i]=1;
					}
				}

				return valueTable;
			}else {
				counter++;
			}
			//failure
			for(int i=0;i<myArrayList.size();i++) {
				if(myArrayList.get(i).isEmpty()) {

					return null;
				}
			}
			counter++;
			//easy case: pure literal
			int[] pure= new int[max];
			boolean havePure=false;
			for(int i=0;i<myArrayList.size();i++) {
				for(int j=0;j<myArrayList.get(i).size();j++) {
					int num=myArrayList.get(i).get(j);
					if(num>0) {
						if(pure[num-1]==0) {
							pure[num-1]=num;
						}else if(pure[num-1]==-num) {
							pure[num-1]=max+1;
						}
					}else if(num<0) {
						if(pure[-num-1]==0) {
							pure[-num-1]=num;
						}else if(pure[-num-1]==-num) {
							pure[-num-1]=max+1;
						}
					}
				}
			}
			for(int i=0;i<pure.length;i++) {
				if(pure[i]!=0&&pure[i]!=max+1) {
					havePure=true;
					valueTable=obviousAssign(pure[i],valueTable);
					for(int j=myArrayList.size()-1;j>=0;j--) {
						if(myArrayList.get(j).contains(pure[i])) {
							myArrayList.remove(j);
						}
					}
					break;
				}
			}
			if(!havePure) {
				counter++;
			}else {
			
				continue;
			}
			//easy case:singleton
			boolean haveSingleton=false;
			for(int i=0;i<myArrayList.size();i++) {
				if(myArrayList.get(i).size()==1) {
					haveSingleton=true;
					valueTable=obviousAssign(myArrayList.get(i).get(0),valueTable);
					myArrayList=propagate(myArrayList.get(i).get(0),myArrayList,valueTable);
					break;
				}
			}
			if(!haveSingleton) {
				counter++;
			}else {
				
				continue;
			}
		}
		//hard case: pick some atom and try each assignment in turn
		int tryNumber=0;
		for(int i=0;i<max;i++) {
			if(valueTable[i]==0) {
				tryNumber=i+1;
				break;
			}
		}
		if(tryNumber==0) {
			System.out.println("Error happens");
		}

		valueTable[tryNumber-1]=1;
		ArrayList<ArrayList<Integer>> myNewArrayList=copy(myArrayList);
		myNewArrayList=propagate(tryNumber,myNewArrayList,valueTable);
		int [] vnew=dp1(max,myNewArrayList,valueTable.clone());
		if(vnew!=null) {
			return vnew;
		}

		valueTable[tryNumber-1]=-1;
		myNewArrayList=propagate(tryNumber,myArrayList,valueTable);
		return dp1(max,myNewArrayList,valueTable.clone());

	}
	
	//propagate the effect of assigning atom A to be value V
	//delete every clause where A appears with sign V
	//delete every literal where A appears with sign not V
	static ArrayList<ArrayList<Integer>> propagate(int a, ArrayList<ArrayList<Integer>> myArrayList,int[] valueTable) {
		int atomA;
		if(a>0) {
			atomA=a;
		}else {
			atomA=-a;
		}
		for(int i=myArrayList.size()-1;i>=0;i--) {
			if((myArrayList.get(i).contains(atomA)&&valueTable[atomA-1]==1)
					||(myArrayList.get(i).contains(-atomA) && valueTable[atomA-1]==-1)) {
				myArrayList.remove(i);
			}else if(myArrayList.get(i).contains(atomA)&&valueTable[atomA-1]==-1) {
				ArrayList<Integer> temp1=new ArrayList<Integer>();
				temp1.add(atomA);
				myArrayList.get(i).removeAll(temp1);
			}else if(myArrayList.get(i).contains(-atomA)&&valueTable[atomA-1]==1) {
				ArrayList<Integer> temp2=new ArrayList<Integer>();
				temp2.add(-atomA);
				myArrayList.get(i).removeAll(temp2);
			}
		}
		return myArrayList;
	}
	
	//given a literal ,make element in value table the correct sign
	static int[] obviousAssign(int a,int[] vt) {
		if(a>0) {
			vt[a-1]=1;
		}else if(a<0) {
			vt[-a-1]=-1;
		}
		return vt;
	}
	
	//copy an arraylist of arraylists
	static ArrayList<ArrayList<Integer>> copy(ArrayList<ArrayList<Integer>> myArrayList){
		ArrayList<ArrayList<Integer>> newlist=new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<myArrayList.size();i++) {
			ArrayList<Integer> thisList=myArrayList.get(i);
			ArrayList<Integer> newThis=new ArrayList<Integer>();
			for(int j=0;j<thisList.size();j++) {
				newThis.add(thisList.get(j));
			}
			newlist.add(newThis);
		}
		return newlist;
	}
}
