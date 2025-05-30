package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import seedu.address.model.household.Household;

/**
 * A UI component that displays information of a {@code Household}.
 */
public class HouseholdCard extends UiPart<Region> {

    private static final String FXML = "HouseholdCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     */
    public final Household household;

    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label householdId;
    @FXML
    private Label address;
    @FXML
    private Label contact;
    @FXML
    private FlowPane tags;

    /**
     * Constructs a {@code HouseholdCard} with the specified household and displayed index.
     *
     * <p>This constructor initializes the card to display the information of the given {@code Household} object
     * at the specified index. It sets up the household's ID, name, address,
     * contact, and tags in the corresponding fields.</p>
     *
     * @param household The {@code Household} object whose information is to be displayed on the card.
     * @param displayedIndex The index at which the household appears in the list, used to display its position.
     */
    public HouseholdCard(Household household, int displayedIndex) {
        super(FXML);
        assert household != null : "Household must not be null";
        assert displayedIndex >= 0 : "Displayed index must be non-negative";
        this.household = household;

        // Set spacing between tag labels
        tags.setHgap(4); // horizontal space between tags
        tags.setVgap(4); // vertical space if tags wrap

        id.setText(displayedIndex + ". ");
        householdId.setText(household.getId().toString());
        name.setText(household.getName().toString());
        address.setText(household.getAddress().toString());
        contact.setText(household.getContact().toString());
        household.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> {
                    Label tagLabel = new Label(tag.tagName);
                    tagLabel.getStyleClass().add("tag-label");
                    tags.getChildren().add(tagLabel);
                });
        //Ensure tags populated correctly
        int tagCount = household.getTags().size();
        assert tags.getChildren().size() == tagCount
                : "Mismatch between tags in model and tag labels in UI";
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof HouseholdCard)) {
            return false;
        }

        // state check
        HouseholdCard card = (HouseholdCard) other;
        return id.getText().equals(card.id.getText())
                && household.equals(card.household);
    }
}
