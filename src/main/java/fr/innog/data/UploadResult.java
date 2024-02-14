package fr.innog.data;

public class UploadResult {
	
		private boolean canUploaded = false;
		
		public String detail;
		
		public Double errorCode;
		
		public UploadResult()
		{

		}
		
		public UploadResult(boolean canUpload, String detail)
		{
			this.canUploaded = canUpload;
			this.detail = detail;
		}
		
		public boolean canUploaded()
		{
			return this.canUploaded;
		}
		
		public String getUploadDetail()
		{
			return detail;
		}
	
}