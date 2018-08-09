package jobs;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import dao.DAO;

/*@Component("cleanDataSharingTasklet")*/
public class CleanDataSharingTasklet implements Tasklet {

	/*@Autowired*/
	private DAO dao;

	public CleanDataSharingTasklet() {
		
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	@Override
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
		// TODO Auto-generated method stub

		try {
			// dao.getJdbcTemplate().update("DELETE FROM MTN_DATA_MONTH_SHARING_EBA");
			dao.getJdbcTemplate().update("TRUNCATE TABLE MTN_DATA_MONTH_SHARING_EBA");

			stepContribution.setExitStatus(ExitStatus.COMPLETED);
			return RepeatStatus.FINISHED;			
		}

		catch(Throwable th) {
			throw th;
		}

		// return null;
	}

}
