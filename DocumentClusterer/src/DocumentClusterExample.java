public class DocumentClusterExample {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		long s = System.currentTimeMillis();
		ClusterAlg.clustering();
		long e = System.currentTimeMillis();
		System.out.println("Time Taken: ");
		System.out.print(e-s);

	}

}
