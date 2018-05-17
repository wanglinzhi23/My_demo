/*
 * This file is part of the LIRE project: http://www.semanticmetadata.net/lire
 * LIRE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * LIRE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LIRE; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * We kindly ask you to refer the any or one of the following publications in
 * any publication mentioning or employing Lire:
 *
 * Lux Mathias, Savvas A. Chatzichristofis. Lire: Lucene Image Retrieval –
 * An Extensible Java CBIR Library. In proceedings of the 16th ACM International
 * Conference on Multimedia, pp. 1085-1088, Vancouver, Canada, 2008
 * URL: http://doi.acm.org/10.1145/1459359.1459577
 *
 * Lux Mathias. Content Based Image Retrieval with LIRE. In proceedings of the
 * 19th ACM International Conference on Multimedia, pp. 735-738, Scottsdale,
 * Arizona, USA, 2011
 * URL: http://dl.acm.org/citation.cfm?id=2072432
 *
 * Mathias Lux, Oge Marques. Visual Information Retrieval using Java and LIRE
 * Morgan & Claypool, 2013
 * URL: http://www.morganclaypool.com/doi/abs/10.2200/S00468ED1V01Y201301ICR025
 *
 * Copyright statement:
 * ====================
 * (c) 2002-2013 by Mathias Lux (mathias@juggle.at)
 *  http://www.semanticmetadata.net/lire, http://www.lire-project.net
 *
 * Updated: 18.01.15 07:31
 */
package net.semanticmetadata.lire.impl;

import net.semanticmetadata.lire.AbstractImageSearcher;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageDuplicates;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.imageanalysis.LireFeature;
import net.semanticmetadata.lire.imageanalysis.sift.FloatArray;
import net.semanticmetadata.lire.utils.ImageUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.util.Bits;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This file is part of the Caliph and Emir project: http://www.SemanticMetadata.net
 * <br>Date: 01.02.2006
 * <br>Time: 00:17:02
 *
 * @author Mathias Lux, mathias@juggle.at
 */
public class GenericFastImageSearcher extends AbstractImageSearcher {
    protected Logger logger = Logger.getLogger(getClass().getName());
    Class<?> descriptorClass;
    String fieldName;
    protected LireFeature cachedInstance = null;
    protected boolean isCaching = false;

    protected LinkedList<byte[]> featureCache;
    protected IndexReader reader;

    protected int maxHits = 10;
    protected TreeSet<SimpleResult> docs;
    protected float minDistance;
    protected boolean useSimilarityScore = false;
	private boolean finish= false;

    /**
     * Creates a new ImageSearcher for the given feature.
     *
     * @param maxHits         the maximum number of hits
     * @param descriptorClass the feature class. It has to implement {@link LireFeature}
     * @param fieldName       a custom field name for the index.
     * @see LireFeature
     */
    public GenericFastImageSearcher(int maxHits, Class<?> descriptorClass, String fieldName) {
        this.maxHits = maxHits;
        docs = new TreeSet<SimpleResult>();
        this.descriptorClass = descriptorClass;
        this.fieldName = fieldName;
        try {
            this.cachedInstance = (LireFeature) this.descriptorClass.newInstance();
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher (" + descriptorClass.getName() + "): " + e.getMessage());
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher (" + descriptorClass.getName() + "): " + e.getMessage());
        }
        init();
    }

    /**
     * Creates a new ImageSearcher for the given feature.
     *
     * @param maxHits            the maximum number of hits
     * @param descriptorClass    the feature class. It has to implement {@link LireFeature}
     * @param fieldName          a custom field name for the index.
     * @param useSimilarityScore return similarity values normalized in [0,1] instead of distance values for results.
     * @see LireFeature
     */
    public GenericFastImageSearcher(int maxHits, Class<?> descriptorClass, String fieldName, boolean useSimilarityScore) {
        this.maxHits = maxHits;
        docs = new TreeSet<SimpleResult>();
        this.descriptorClass = descriptorClass;
        this.fieldName = fieldName;
        this.useSimilarityScore = useSimilarityScore;
        try {
            this.cachedInstance = (LireFeature) this.descriptorClass.newInstance();
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher (" + descriptorClass.getName() + "): " + e.getMessage());
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher (" + descriptorClass.getName() + "): " + e.getMessage());
        }
        init();
    }

    /**
     * Creates a new ImageSearcher for the given feature.
     *
     * @param maxHits         the maximum number of hits
     * @param descriptorClass the feature class. It has to implement {@link LireFeature}
     * @see LireFeature
     */
    public GenericFastImageSearcher(int maxHits, Class<?> descriptorClass) {
        this.maxHits = maxHits;
        docs = new TreeSet<SimpleResult>();
        this.descriptorClass = descriptorClass;
        try {
            this.cachedInstance = (LireFeature) this.descriptorClass.newInstance();
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher (" + descriptorClass.getName() + "): " + e.getMessage());
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher (" + descriptorClass.getName() + "): " + e.getMessage());
        }
        this.fieldName = cachedInstance.getFieldName();
        init();
    }

    protected void init() {
        // put all respective features into an in-memory cache ...
        if (isCaching && reader != null) {
            int docs = reader.numDocs();
            featureCache = new LinkedList<byte[]>();
            try {
                Document d;
                for (int i = 0; i < docs; i++) {
                    d = reader.document(i);
                    cachedInstance.setByteArrayRepresentation(d.getField(fieldName).binaryValue().bytes, d.getField(fieldName).binaryValue().offset, d.getField(fieldName).binaryValue().length);
                    featureCache.add(cachedInstance.getByteArrayRepresentation());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a n ImageSearcher for the given feature. If isCaching is set to true, the features will be hold in memory,
     * which speeds up search significantly. However, this takes sometimes a lot of memory, so use it carefully.
     *
     * @param maxHits         the maximum number of hits
     * @param descriptorClass the feature class. It has to implement {@link LireFeature}
     * @param fieldName       a custom field name for the index.
     * @param isCaching       set to true if you want to search in-memory.
     * @param reader          the IndexReader used for accessing the index.
     */
    public GenericFastImageSearcher(int maxHits, Class<?> descriptorClass, String fieldName, boolean isCaching, IndexReader reader) {
        this.isCaching = isCaching;
        this.maxHits = maxHits;
        docs = new TreeSet<SimpleResult>();
        this.descriptorClass = descriptorClass;
        this.fieldName = fieldName;
        try {
            this.cachedInstance = (LireFeature) this.descriptorClass.newInstance();
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher (" + descriptorClass.getName() + "): " + e.getMessage());
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher (" + descriptorClass.getName() + "): " + e.getMessage());
        }
        this.reader = reader;
        init();
    }

    /**
     * Creates a n ImageSearcher for the given feature. If isCaching is set to true, the features will be hold in memory,
     * which speeds up search significantly. However, this takes sometimes a lot of memory, so use it carefully.
     *
     * @param maxHits         the maximum number of hits
     * @param descriptorClass the feature class. It has to implement {@link LireFeature}
     * @param isCaching
     * @param reader          reader the IndexReader used for accessing the index.
     */
    public GenericFastImageSearcher(int maxHits, Class<?> descriptorClass, boolean isCaching, IndexReader reader) {
        this.isCaching = isCaching;
        this.maxHits = maxHits;
        docs = new TreeSet<SimpleResult>();
        this.descriptorClass = descriptorClass;
        try {
            this.cachedInstance = (LireFeature) this.descriptorClass.newInstance();
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher (" + descriptorClass.getName() + "): " + e.getMessage());
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher (" + descriptorClass.getName() + "): " + e.getMessage());
        }
        this.reader = reader;
        this.fieldName = cachedInstance.getFieldName();
        init();
    }

    public ImageSearchHits search(BufferedImage image, IndexReader reader) throws IOException {
        logger.finer("Starting extraction.");
        LireFeature lireFeature = null;
        SimpleImageSearchHits searchHits = null;
        try {
            lireFeature = (LireFeature) descriptorClass.newInstance();
            // Scaling image is especially with the correlogram features very important!
            BufferedImage bimg = image;
            if (Math.max(image.getHeight(), image.getWidth()) > GenericDocumentBuilder.MAX_IMAGE_DIMENSION) {
                bimg = ImageUtils.scaleImage(image, GenericDocumentBuilder.MAX_IMAGE_DIMENSION);
            }
            lireFeature.extract(bimg);
            logger.fine("Extraction from image finished");

            float maxDistance = findSimilar(reader, lireFeature, 0, new HashMap());
            if (!useSimilarityScore) {
                searchHits = new SimpleImageSearchHits(this.docs, maxDistance);
            } else {
                searchHits = new SimpleImageSearchHits(this.docs, maxDistance, useSimilarityScore);
            }
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher: " + e.getMessage());
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher: " + e.getMessage());
        }
        return searchHits;
    }

    /**
     * @param reader
     * @param lireFeature
     * @param scoreThreshold 
     * @param indexMap 
     * @return the maximum distance found for normalizing.
     * @throws java.io.IOException
     */
    protected float findSimilar(IndexReader reader, LireFeature lireFeature, float scoreThreshold, Map<Integer, Integer> indexMap) throws IOException {
        minDistance = 100F;
//        overallMaxDistance = -1f;

        // clear result set ...
        docs.clear();
        // Needed for check whether the document is deleted.
        Bits liveDocs = MultiFields.getLiveDocs(reader);
        Document d;
        float tmpDistance;
        int docs = reader.numDocs();
        if (!isCaching) {
            // we read each and every document from the index and then we compare it to the query.
        	Date now = new Date();
            for (int i = 0; i < docs; i++) {
                if ((reader.hasDeletions() && !liveDocs.get(i)) || indexMap.containsKey(i)) continue; // if it is deleted, just ignore it.

                d = reader.document(i);
                tmpDistance = getDistance(d, lireFeature);
                if (tmpDistance >= scoreThreshold) {
                	this.docs.add(new SimpleResult(tmpDistance, d, i));
                }
//                if (tmpDistance < -20) continue;
//                // if the array is not full yet:
//                if (this.docs.size() < maxHits) {
//                    this.docs.add(new SimpleResult(tmpDistance, d, i));
//                    if (tmpDistance < minDistance) minDistance = tmpDistance;
//                } else if (tmpDistance > minDistance) {
//                    // if it is nearer to the sample than at least on of the current set:
//                    // remove the last one ...
//                    this.docs.remove(this.docs.first());
//                    // add the new one ...
//                    this.docs.add(new SimpleResult(tmpDistance, d, i));
//                    // and set our new distance border ...
//                    minDistance = this.docs.first().getDistance();
//                }
            }
            System.out.println("cost first time:"+(new Date().getTime()-now.getTime()));
        } else {
            // we use the in-memory cache to find the matching docs from the index.
            int count = 0;
            for (Iterator<byte[]> iterator = featureCache.iterator(); iterator.hasNext(); ) {
                cachedInstance.setByteArrayRepresentation(iterator.next());
                tmpDistance = lireFeature.getDistance(cachedInstance);
                assert (tmpDistance >= 0);
                // if the array is not full yet:
                if (this.docs.size() < maxHits) {
                    this.docs.add(new SimpleResult(tmpDistance, reader.document(count), count));
                    if (tmpDistance > minDistance) minDistance = tmpDistance;
                } else if (tmpDistance < minDistance) {
                    // if it is nearer to the sample than at least on of the current set:
                    // remove the last one ...
                    this.docs.remove(this.docs.last());
                    // add the new one ...
                    this.docs.add(new SimpleResult(tmpDistance, reader.document(count), count));
                    // and set our new distance border ...
                    minDistance = this.docs.last().getDistance();
                }
                count++;
            }
        }
        return minDistance;
    }
    


    /**
     * @param reader
     * @param lireFeature
     * @return the maximum distance found for normalizing.
     * @throws java.io.IOException
     */
    protected float findSimilarByMul(final IndexReader reader, final LireFeature lireFeature) throws IOException {
    	finish = false;
        minDistance = Float.MAX_VALUE;
//        overallMaxDistance = -1f;
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1000, 5000, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE), new ThreadPoolExecutor.CallerRunsPolicy());   
        // clear result set ...
        docs.clear();
        // Needed for check whether the document is deleted.
        Bits liveDocs = MultiFields.getLiveDocs(reader);
        Document d;
        float tmpDistance = 0;
        int docs = reader.numDocs();
//        final List<Float> testList = Collections.synchronizedList(new LinkedList<Float>());
        if (!isCaching) {
        	Date now = new Date();
        	for (int i = 0; i < docs; i++) {

                if (reader.hasDeletions() && !liveDocs.get(i)) continue; // if it is deleted, just ignore it.
//                docsNum++;
                final int no = i;
                threadPool.submit(new Runnable() {
                    Document d = null;
            		public void run() {
            			// TODO Auto-generated method stub
                        try {
    						d = reader.document(no);
    						float dis = getDistance(d, lireFeature);
    		                if (no != (reader.numDocs()-1)&&dis < -20) return;
    						addDocs(dis , d, no, reader);
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
            		}
            	});
            }
        	while(!finish) {
        		try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	System.out.println("cost first time:"+(new Date().getTime()-now.getTime()));
//        	now = new Date();
            // we read each and every document from the index and then we compare it to the query.
//            for (int i = 0; i < docsNum; i++) {
//                if (reader.hasDeletions() && !liveDocs.get(i)) continue; // if it is deleted, just ignore it.
//
//                d = reader.document(i);
////				tmpDistance = distances.get(i).get();
////            	while(i>=docsNum) {
////    					Thread.sleep(1);
////            	}
//				tmpDistance = testList.get(i);
//            
//            }
//        	System.out.println("cost second time:"+(new Date().getTime()-now.getTime()));
        } else {
            // we use the in-memory cache to find the matching docs from the index.
            int count = 0;
            for (Iterator<byte[]> iterator = featureCache.iterator(); iterator.hasNext(); ) {
                cachedInstance.setByteArrayRepresentation(iterator.next());
                tmpDistance = lireFeature.getDistance(cachedInstance);
                assert (tmpDistance >= 0);
                // if the array is not full yet:
                if (this.docs.size() < maxHits) {
                    this.docs.add(new SimpleResult(tmpDistance, reader.document(count), count));
                    if (tmpDistance > minDistance) minDistance = tmpDistance;
                } else if (tmpDistance < minDistance) {
                    // if it is nearer to the sample than at least on of the current set:
                    // remove the last one ...
                    this.docs.remove(this.docs.last());
                    // add the new one ...
                    this.docs.add(new SimpleResult(tmpDistance, reader.document(count), count));
                    // and set our new distance border ...
                    minDistance = this.docs.last().getDistance();
                }
                count++;
            }
        }
        threadPool.shutdown();
        return minDistance;
    }

    protected synchronized void addDocs(float tmpDistance, Document d, int i, IndexReader reader) {
    	 // if the array is not full yet:
        if (this.docs.size() < maxHits) {
            this.docs.add(new SimpleResult(tmpDistance, d, i));
            if (tmpDistance < minDistance) minDistance = tmpDistance;
        } else if (tmpDistance > minDistance) {
            // if it is nearer to the sample than at least on of the current set:
            // remove the last one ...
            this.docs.remove(this.docs.first());
            // add the new one ...
            this.docs.add(new SimpleResult(tmpDistance, d, i));
            // and set our new distance border ...
            minDistance = this.docs.first().getDistance();
        }
        if(i == (reader.numDocs()-1)) finish = true;
    }
    
    /**
     * Main similarity method called for each and every document in the index.
     *
     * @param document
     * @param lireFeature
     * @return the distance between the given feature and the feature stored in the document.
     */
    protected float getDistance(Document document, LireFeature lireFeature) {
        if (document.getField(fieldName).binaryValue() != null && document.getField(fieldName).binaryValue().length > 0) {
        	LireFeature cachedInstance = null;
			try {
				cachedInstance = (LireFeature) descriptorClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            cachedInstance.setByteArrayRepresentation(document.getField(fieldName).binaryValue().bytes, document.getField(fieldName).binaryValue().offset, document.getField(fieldName).binaryValue().length);
            return lireFeature.getDistance(cachedInstance);
        } else {
            logger.warning("No feature stored in this document! (" + descriptorClass.getName() + ")");
        }
        return 0f;
    }

    public ImageSearchHits search(Document doc, IndexReader reader) throws IOException {
        SimpleImageSearchHits searchHits = null;
        try {
            LireFeature lireFeature = (LireFeature) descriptorClass.newInstance();

            if (doc.getField(fieldName).binaryValue() != null && doc.getField(fieldName).binaryValue().length > 0)
                lireFeature.setByteArrayRepresentation(doc.getField(fieldName).binaryValue().bytes, doc.getField(fieldName).binaryValue().offset, doc.getField(fieldName).binaryValue().length);
            float maxDistance = findSimilar(reader, lireFeature, 0, new HashMap());

            if (!useSimilarityScore) {
                searchHits = new SimpleImageSearchHits(this.docs, maxDistance);
            } else {
                searchHits = new SimpleImageSearchHits(this.docs, maxDistance, useSimilarityScore);
            }
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher: " + e.getMessage());
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher: " + e.getMessage());
        }
        return searchHits;
    }

    public ImageDuplicates findDuplicates(IndexReader reader) throws IOException {
        // get the first document:
        SimpleImageDuplicates simpleImageDuplicates = null;
        try {
//            if (!IndexReader.indexExists(reader.directory()))
//                throw new FileNotFoundException("No index found at this specific location.");
            Document doc = reader.document(0);

            LireFeature lireFeature = (LireFeature) descriptorClass.newInstance();
            if (doc.getField(fieldName).binaryValue() != null && doc.getField(fieldName).binaryValue().length > 0)
                lireFeature.setByteArrayRepresentation(doc.getField(fieldName).binaryValue().bytes, doc.getField(fieldName).binaryValue().offset, doc.getField(fieldName).binaryValue().length);

            HashMap<Float, List<String>> duplicates = new HashMap<Float, List<String>>();

            // Needed for check whether the document is deleted.
            Bits liveDocs = MultiFields.getLiveDocs(reader);

            int docs = reader.numDocs();
            int numDuplicates = 0;
            for (int i = 0; i < docs; i++) {
                if (reader.hasDeletions() && !liveDocs.get(i)) continue; // if it is deleted, just ignore it.

                Document d = reader.document(i);
                float distance = getDistance(d, lireFeature);

                if (!duplicates.containsKey(distance)) {
                    duplicates.put(distance, new LinkedList<String>());
                } else {
                    numDuplicates++;
                }
                duplicates.get(distance).add(d.getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue());
            }

            if (numDuplicates == 0) return null;

            LinkedList<List<String>> results = new LinkedList<List<String>>();
            for (float f : duplicates.keySet()) {
                if (duplicates.get(f).size() > 1) {
                    results.add(duplicates.get(f));
                }
            }
            simpleImageDuplicates = new SimpleImageDuplicates(results);
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher: " + e.getMessage());
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher: " + e.getMessage());
        }
        return simpleImageDuplicates;

    }

    public String toString() {
        return "GenericSearcher using " + descriptorClass.getName();
    }

	public ImageSearchHits search(byte[] feature, IndexReader reader, float scoreThreshold, Map<Integer, Integer> indexMap) throws IOException {
        logger.finer("Starting extraction.");
        LireFeature lireFeature = null;
        SimpleImageSearchHits searchHits = null;
        try {
            lireFeature = (LireFeature) descriptorClass.newInstance();
            // Scaling image is especially with the correlogram features very important!
            lireFeature.setByteArrayRepresentation(feature);
            logger.fine("Extraction from image finished");

            float maxDistance = findSimilar(reader, lireFeature, scoreThreshold, indexMap);
            if (!useSimilarityScore) {
                searchHits = new SimpleImageSearchHits(this.docs, maxDistance);
            } else {
                searchHits = new SimpleImageSearchHits(this.docs, maxDistance, useSimilarityScore);
            }
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher: " + e.getMessage());
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error instantiating class for generic image searcher: " + e.getMessage());
        }
        return searchHits;
    }

}
