package jobs;

import java.util.Date;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import dao.DAO;
import dao.queries.JdbcUSSDServiceDao;
import domain.models.USSDService;
import product.ProductProperties;

/*@Component("cleanDataSharingTasklet")*/
public class CleanDataSharingTasklet implements Tasklet {

	/*@Autowired*/
	private DAO dao;

	/*@Autowired*/
	private ProductProperties productProperties;

	public CleanDataSharingTasklet() {
		
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	@Override
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
		// TODO Auto-generated method stub

		try {
			/*Although Spring Batch stores the job parameters in an instance of the JobParameter class, when you 
			obtain the parameters this way getJobParameters() returns a Map<String, Object>. Because of this, the cast is required.*/
			// String param = (String) chunkContext.getStepContext().getJobParameters().get("paramName");


			USSDService service = new JdbcUSSDServiceDao(dao).getOneUSSDService(productProperties.getSc());
			// Date now = (chunkContext.getStepContext().getStepExecution().getStartTime() == null) ? new Date() : (Date) chunkContext.getStepContext().getStepExecution().getStartTime().clone();
			Date now = (chunkContext.getStepContext().getStepExecution().getJobExecution().getStartTime() == null) ? new Date() : (Date) chunkContext.getStepContext().getStepExecution().getJobExecution().getStartTime().clone();

			/*The first way to stop execution is to throw an exception. This works all the time, unless you configured the job to skip some exceptions in a chunk-oriented step!*/
			// Stopping a job from a tasklet : Setting the stop flag in a tasklet is straightforward;
			if((service == null) || (((service.getStart_date() != null) && (now.before(service.getStart_date()))) || ((service.getStop_date() != null) && (now.after(service.getStop_date()))))) {
				// Sets stop flag
				chunkContext.getStepContext().getStepExecution().setTerminateOnly();
				stepContribution.setExitStatus(new ExitStatus("STOPPED", "Job should not be run right now."));
			}
			else {
				// dao.getJdbcTemplate().update("DELETE FROM MTN_DATA_MONTH_SHARING_EBA");
				dao.getJdbcTemplate().update("TRUNCATE TABLE MTN_DATA_MONTH_SHARING_EBA");

				stepContribution.setExitStatus(ExitStatus.COMPLETED);
				return RepeatStatus.FINISHED;
			}
		}

		catch(Throwable th) {
			throw th;
		}

		return null;
	}

}
