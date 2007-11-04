package test.it.unimi.dsi.sux4j.mph;

import it.unimi.dsi.fastutil.io.BinIO;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.sux4j.bits.BitVector;
import it.unimi.dsi.sux4j.bits.BitVectors;
import it.unimi.dsi.sux4j.bits.LongArrayBitVector;
import it.unimi.dsi.sux4j.mph.HollowTrie;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

public class HollowTrieTest extends TestCase {

	public static ObjectArrayList<BitVector> listOf( final int[]... bit ) {
		ObjectArrayList<BitVector> vectors = new ObjectArrayList<BitVector>();
		for( int[] v : bit ) vectors.add( LongArrayBitVector.of(  v  ) );
		return vectors;
	}

	@SuppressWarnings("unchecked")
	public void testEmpty() {
		HollowTrie hollowTrie = new HollowTrie( listOf( new int[][] {} ), BitVectors.identity() );
		assertEquals( -1, hollowTrie.getLeafIndex( LongArrayBitVector.of( 0 ) ) );
		assertEquals( -1, hollowTrie.getLeafIndex( LongArrayBitVector.of( 1 ) ) );
	}

	@SuppressWarnings("unchecked")
	public void testSimple() {
		HollowTrie hollowTrie = new HollowTrie( 
				listOf( new int[][] { { 0 }, { 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 1 }, { 1, 0, 0, 1 } } ).iterator(), BitVectors.identity()  );

		assertEquals( 0, hollowTrie.getLeafIndex( LongArrayBitVector.of( 0 ) ) );
		assertEquals( 1, hollowTrie.getLeafIndex( LongArrayBitVector.of( 1, 0, 0, 0, 0 ) ) );
		assertEquals( 2, hollowTrie.getLeafIndex( LongArrayBitVector.of( 1, 0, 0, 0, 1 ) ) );
		assertEquals( 3, hollowTrie.getLeafIndex( LongArrayBitVector.of( 1, 0, 0, 1 ) ) );


		hollowTrie = new HollowTrie( 
				listOf( new int[][] { { 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0 }, { 0, 1, 0, 1, 0, 0 }, { 0, 1, 0, 1, 0, 1 }, { 0, 1, 1, 1, 0 } } ).iterator(), BitVectors.identity()  );

		assertEquals( 0, hollowTrie.getLeafIndex( LongArrayBitVector.of( 0, 0, 0, 0, 0 ) ) );
		assertEquals( 1, hollowTrie.getLeafIndex( LongArrayBitVector.of( 0, 1, 0, 0, 0 ) ) );
		assertEquals( 2, hollowTrie.getLeafIndex( LongArrayBitVector.of( 0, 1, 0, 1, 0, 0 ) ) );
		assertEquals( 3, hollowTrie.getLeafIndex( LongArrayBitVector.of( 0, 1, 0, 1, 0, 1 ) ) );
		assertEquals( 4, hollowTrie.getLeafIndex( LongArrayBitVector.of( 0, 1, 1, 1, 0 ) ) );
	}

	@SuppressWarnings("unchecked")
	public void testRandom() throws IOException, ClassNotFoundException {
		Random r = new Random( 3 );
		final int n = 10;
		final LongArrayBitVector[] bitVector = new LongArrayBitVector[ n ];
		for( int i = 0; i < n; i++ ) {
			bitVector[ i ] = LongArrayBitVector.getInstance();
			int l = 12;
			while( l-- != 0 ) bitVector[ i ].add( r.nextBoolean() );
		}
		
		// Sort lexicographically
		Arrays.sort( bitVector );
		
		HollowTrie<LongArrayBitVector> hollowTrie = new HollowTrie<LongArrayBitVector>( Arrays.asList( bitVector ), BitVectors.identity() );
		
		for( int i = 0; i < n; i++ ) assertEquals( i, hollowTrie.getLeafIndex( bitVector[ i ] ) );

		// Test serialisation
		final File temp = File.createTempFile( getClass().getSimpleName(), "test" );
		temp.deleteOnExit();
		BinIO.storeObject( hollowTrie, temp );
		hollowTrie = (HollowTrie<LongArrayBitVector>)BinIO.loadObject( temp );

		for( int i = 0; i < n; i++ ) assertEquals( i, hollowTrie.getLeafIndex( bitVector[ i ] ) );

		// Test that random inquiries do not break the trie
		for( int i = 0; i < 10; i++ ) {
			bitVector[ i ] = LongArrayBitVector.getInstance();
			int l = 8;
			while( l-- != 0 ) bitVector[ i ].add( r.nextBoolean() );
		}
	}
}
