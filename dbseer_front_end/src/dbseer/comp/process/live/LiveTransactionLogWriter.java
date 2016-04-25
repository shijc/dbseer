/*
 * Copyright 2013 Barzan Mozafari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dbseer.comp.process.live;

import dbseer.comp.data.Transaction;
import dbseer.comp.process.transaction.TransactionLogProcessor;
import dbseer.comp.process.transaction.TransactionLogWriter;
import dbseer.gui.DBSeerExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Dong Young Yoon on 1/4/16.
 */
public class LiveTransactionLogWriter implements Runnable
{
	private TransactionLogProcessor processor;
	private TransactionLogWriter writer;

	public LiveTransactionLogWriter(TransactionLogProcessor processor, TransactionLogWriter writer)
	{
		this.processor = processor;
		this.writer = writer;
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				Set<Long> timestamps = processor.getTimestamps();
				if (timestamps.size() > 1)
				{
					List<Long> sortedTime = new ArrayList<Long>(timestamps);
					Collections.sort(sortedTime);
					for (int i = 0; i < sortedTime.size() - 1; ++i)
					{
						Long time = sortedTime.get(i);
						List<Transaction> transactions = processor.getTransactions(time);

						writer.writeLog(time.longValue(), transactions);
					}
				}
				else
				{
					Thread.sleep(200);
				}
			}
			catch (Exception e)
			{
				if (e instanceof InterruptedException)
				{
					// do nothing for now.
				}
				else
				{
					DBSeerExceptionHandler.handleException(e);
				}
				return;
			}
		}
	}
}