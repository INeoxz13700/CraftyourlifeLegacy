package fr.innog.common;

import fr.innog.api.informations.ApiInformations;
import fr.innog.common.controllers.AuthentificationController;
import fr.innog.common.controllers.ConcessionnaireController;
import fr.innog.common.controllers.FireController;
import fr.innog.common.controllers.GarageController;
import fr.innog.common.controllers.PlayerController;
import fr.innog.common.controllers.ReanimationController;
import fr.innog.common.controllers.ThirstController;
import fr.innog.common.controllers.UIRemoteController;

public class ModControllers {

    public static final ConcessionnaireController concessionnaireController = new ConcessionnaireController(ApiInformations.getMysql());

    public static final UIRemoteController uiController = new UIRemoteController();

    public static final GarageController garageController = new GarageController();

    public static final ThirstController thirstController = new ThirstController();
    
    public static final ReanimationController reanimationController = new ReanimationController();

    public static final PlayerController playerController = new PlayerController();
   
    public static final AuthentificationController authController = new AuthentificationController();
   
    public static final FireController fireController = new FireController();

    
}
