import PopupChoice from "./PopupChoice";
import {useContext} from "preact/hooks";
import GameState from "../../../contexts/GameState";
import CardImage from "../card/CardImage";
import HorizontalCenter from "../../layoutUtils/HorizontalCenter";
import BannerText from "../../layoutUtils/BannerText";
import DecisionMaker from "../../../contexts/DecisionMaker";

export default function ModeChoice() {
    const gameState = useContext(GameState);
    const decisionMaker = useContext(DecisionMaker);
    const {modes, artDetails} = gameState.currentChoice.details;
    return <PopupChoice text="Choose Mode">
        <HorizontalCenter>
            {modes.map((mode, index) =>
                <CardImage card={{text: mode, artDetails}}
                           showText={true}
                           className="ml-2"
                           onClick={() => decisionMaker.makeDecision(index)}
                           style={{cursor: "pointer"}}/>
            )}
        </HorizontalCenter>
        <BannerText>Choose Mode</BannerText>
    </PopupChoice>;
}
