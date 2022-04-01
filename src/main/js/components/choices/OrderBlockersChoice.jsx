import _ from "lodash";
import {useCallback, useContext, useState} from "preact/hooks";
import {DragDropContext, Draggable, Droppable} from 'react-beautiful-dnd';
import GameState from "../../contexts/GameState";
import BannerText from "../layoutUtils/BannerText";
import HorizontalCenter from "../layoutUtils/HorizontalCenter";
import CardImage from "../card/CardImage";
import DecisionButton from "../DecisionButton";
import PopupChoice from "./PopupChoice";

const reorder = (list, startIndex, endIndex) => {
    const result = Array.from(list);
    const [removed] = result.splice(startIndex, 1);
    result.splice(endIndex, 0, removed);

    return result;
};

export default function OrderBlockersChoice() {
    const gameState = useContext(GameState);
    const [orderedBlockerIds, setOrderedBlockerIds] = useState(gameState.currentChoice.details.blockers);

    const player = gameState.player;
    const opponent = _.find(gameState.gameData.playersInTurnOrder, p => p !== player);

    const attacker = _.find(gameState.battlefield[player], o => o.objectId === gameState.currentChoice.details.attacker);
    const blockers = _.map(orderedBlockerIds, blocker => _.find(gameState.battlefield[opponent], o => o.objectId === blocker));

    const onDragEnd = useCallback((result) => {
        // dropped outside the list
        if (!result.destination) {
            return;
        }
        const newBlockerIds = reorder(
            orderedBlockerIds,
            result.source.index,
            result.destination.index);
        setOrderedBlockerIds(newBlockerIds);
    }, [orderedBlockerIds]);

    return <PopupChoice text="Order Blockers">
        <DragDropContext onDragEnd={onDragEnd}>
            <Droppable droppableId="droppable" direction="horizontal">
                {(provided) => (
                    <HorizontalCenter ref={provided.innerRef}{...provided.droppableProps}>
                        {blockers.map((blocker, index) => (
                            <Draggable key={blocker.objectId} draggableId={blocker.objectId.toString()} index={index}>
                                {(provided) => (
                                    <div
                                        ref={provided.innerRef}
                                        {...provided.draggableProps}
                                        {...provided.dragHandleProps}
                                        style={{
                                            display: "inline-block",
                                            marginLeft: index > 0 && "-100px",
                                            ...provided.draggableProps.style,
                                            zIndex: blockers.length - index
                                        }}
                                    >
                                        <CardImage card={blocker}/>
                                    </div>
                                )}
                            </Draggable>
                        ))}
                        {provided.placeholder}
                    </HorizontalCenter>
                )}
            </Droppable>
        </DragDropContext>
        <HorizontalCenter>
            <CardImage card={attacker}/>
        </HorizontalCenter>
        <BannerText>Order Blockers</BannerText>
        <HorizontalCenter>
            <DecisionButton optionToChoose={orderedBlockerIds.join(" ")}>Submit</DecisionButton>
        </HorizontalCenter>
    </PopupChoice>;
}
