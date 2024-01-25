import org.apache.commons.io.IOUtils;
import  com.neuronrobotics.bowlerstudio.physics.*;
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.bowlerstudio.threed.*;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR
import com.neuronrobotics.sdk.common.DeviceManager
import com.neuronrobotics.sdk.util.ThreadUtil
MobileBase base;
if(args==null){
		base=DeviceManager.getSpecificDevice( 
			"GOOBY"
			,{
				MobileBase b= ScriptingEngine.gitScriptRun(	"https://github.com/dromero2025/GOOBY.git"
				, "GOOBY.xml"
				, null )
				b.connect()
				return b
				})
	}else
		base=args.get(0)
class translator{
	
	println "Now we will move just one leg"
	DHParameterKinematics leg0 = base.getAllDHChains().get(0)
	
	double zLift=10
	println "Start from where the arm already is and move it from there with absolute location"
	TransformNR current = leg0.calcHome()
	TransformNR homereference = current.copy()
	println current
	current.translateY(zLift);
	//TransformNR ofset = current.times(homereference.inverse())
	TransformNR offset = homereference.inverse()
	TransformNR offseter = offset.times(current)
	for(double i=0;i<1;i+=0.01) {
		TransformNR tmp = offseter.scale(i);
		TransformNR step = homereference.times(tmp)
		println step
		leg0.setDesiredTaskSpaceTransform(step,  1000.0);
		ThreadUtil.wait(10)// wait for the legs to fully arrive
	}
	
	println "and reset it"
	current.translateZ(-zLift);
	leg0.setDesiredTaskSpaceTransform(current,  2.0);
	ThreadUtil.wait(2000)// wait for the legs to fully arrive

}
return null;