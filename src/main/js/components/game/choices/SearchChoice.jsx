import {useContext, useState} from "preact/hooks";
import {Button, Modal} from "react-bootstrap";
import GameState from "../../../contexts/GameState";
import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";
import _ from "lodash";
import CardImage from "../card/CardImage";
import DecisionButton from "../DecisionButton";

export default function SearchChoice() {
    const gameState = useContext(GameState);
    const [showModal, setShowModal] = useState(true);
    const [chosenOption, setChosenOption] = useState(null);
    const options = gameState.currentChoice.details.possibleChoices;

    return <div>
        <BannerText as="p">Choose a Card</BannerText>
        <HorizontalCenter>
            {!showModal && <Button onClick={() => setShowModal(true)}>Choose</Button>}
        </HorizontalCenter>
        <Modal show={showModal} onHide={() => setShowModal(false)} dialogClassName="search-dialog">
            <Modal.Body>
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
                            return <CardImage card={card} style={style} onClick={onClick} className={className}/>
                        })}
                    </HorizontalCenter>
                </div>
                <HorizontalCenter>
                    <DecisionButton optionToChoose={chosenOption && chosenOption.toString()} disabled={!chosenOption}>Submit</DecisionButton>
                </HorizontalCenter>
            </Modal.Body>
        </Modal>
    </div>
}
