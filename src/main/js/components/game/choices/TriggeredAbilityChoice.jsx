import {useContext} from "preact/hooks";
import DecisionMaker from "../../../contexts/DecisionMaker";
import GameState from "../../../contexts/GameState";
import BannerText from "../../layout/BannerText";
import CardRow from "../card/CardRow";
import CardWithText from "../card/CardWithText";
import PopupChoice from "./PopupChoice";

export default function TriggeredAbilityChoice() {
    const gameState = useContext(GameState);
    const decisionMaker = useContext(DecisionMaker);

    function Ability({card, ...props}) {
        return <CardWithText card={card} {...props} onClick={() => decisionMaker.makeDecision(card.id)}/>
    }

    return <PopupChoice text="Choose a triggered ability to put on the stack">
        <CardRow cards={gameState.currentChoice.details.abilities} as={Ability} />
        <BannerText as="h3">Choose a triggered ability to put on the stack</BannerText>
    </PopupChoice>
}
