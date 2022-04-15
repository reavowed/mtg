import {useContext, useState} from "preact/hooks";
import GameState from "../../contexts/GameState";
import {addClass} from "../../utils/element-utils";
import HorizontalCenter from "../layoutUtils/HorizontalCenter";
import _ from "lodash";
import CardWithText from "../card/CardWithText";
import DecisionButton from "../DecisionButton";
import PopupChoice from "./PopupChoice";

export default function SearchLibraryChoice() {
    const gameState = useContext(GameState);
    const [chosenOption, setChosenOption] = useState(null);
    const options = gameState.currentChoice.details.possibleChoices;

    return <PopupChoice text="Choose a Card">
        <div style={{overflowX: "scroll"}}>
            <HorizontalCenter>
                {_.map(gameState.libraries[gameState.player], (card, index) => {
                    const canChoose = _.includes(options, card.objectId);
                    const isChosen = card.objectId === chosenOption;
                    const style = {
                        marginLeft: index > 0 && "-75px",
                    };
                    const onClick = () => {
                        if (isChosen) {
                            setChosenOption(null);
                        } else if (canChoose) {
                            setChosenOption(card.objectId);
                        }
                    }
                    const className = isChosen ? "selected" : canChoose ? "selectable" : "non-selectable";
                    return <CardWithText card={card} style={style} onClick={onClick} className={addClass(className, "my-2")}/>
                })}
            </HorizontalCenter>
        </div>
        <HorizontalCenter>
            <DecisionButton optionToChoose={chosenOption && chosenOption.toString()} disabled={!chosenOption}>Submit</DecisionButton>
        </HorizontalCenter>
    </PopupChoice>;
}
