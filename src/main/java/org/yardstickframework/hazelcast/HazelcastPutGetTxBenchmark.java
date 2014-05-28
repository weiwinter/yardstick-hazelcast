/*
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.yardstickframework.hazelcast;

import com.hazelcast.core.*;
import com.hazelcast.transaction.*;

import static com.hazelcast.transaction.TransactionOptions.TransactionType.*;

/**
 * Hazelcast benchmark that performs transactional put and get operations.
 */
public class HazelcastPutGetTxBenchmark extends HazelcastAbstractBenchmark {
    /** */
    public HazelcastPutGetTxBenchmark() {
        super("map");
    }

    /** {@inheritDoc} */
    @Override public void test() throws Exception {
        int key = nextRandom(0, args.range() / 2);

        // Repeatable read isolation level is always used.
        TransactionOptions txOpts = new TransactionOptions().setTransactionType(TWO_PHASE);

        TransactionContext ctx = hazelcast().newTransactionContext(txOpts);

        ctx.beginTransaction();

        TransactionalMap<Object, Object> txMap = ctx.getMap("map");

        try {
            Object val = txMap.get(key);

            if (val != null)
                key = nextRandom(args.range() / 2, args.range());

            txMap.put(key, new SampleValue(key));

            ctx.commitTransaction();
        }
        catch (Exception e) {
            e.printStackTrace();

            ctx.rollbackTransaction();
        }
    }
}