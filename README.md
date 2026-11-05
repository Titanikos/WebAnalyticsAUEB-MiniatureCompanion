# Mini Companion

## The idea

Building and painting miniatures involves choosing among many products from different companies, and the information is scattered across forums, shop sites and videos. A hobbyist often has to work out by hand:

- which third-party conversion parts actually fit a kit.
- which paints achieve a given look, and which cheaper brands can replace the expensive ones.
- which of those paints are sold in their country, and whether it is worth ordering from overseas.
- which tutorials show how to paint the model they are working on.

Miniature Companion puts those answers in one place, starting from the model.

### What makes it different

Many existing hobby apps are paint-conversion charts, collection trackers, or shop catalogs. This app is built around the recommendation: starting from one model, it ranks compatible parts, paints with cheaper alternatives, and tutorials, and it explains each suggestion. (This comparison is based on a quick look at the category and should be checked against the app stores.)

## Recommender system

The app is a **recommender system**. For each candidate item (a part, paint, tool, brush) it estimates how useful that item is for the user's chosen model.

**Mockup scoring** (`Recommender.kt`):

```
match = 0.5 * compatibility + 0.3 * fit with the planned paint scheme + 0.2 * popularity
```

Each input is a 0 to 100 value. Items are then filtered by region (unless "Include overseas shops" is on) and ranked by score. The result is shown as a percentage with a label (Great fit, Good fit, Possible fit) and a one-line reason, and low-confidence items carry a warning, such as a scale check.

In the mockup the three inputs are made-up numbers in `Data.kt`. In a real version they would come from:

| Input | Real data source (planned) |
|---|---|
| Compatibility | Community and manually tagged data on which parts fit which kits, plus manufacturer information |
| Scheme fit | The user's chosen colour scheme, matched to paint recipes and tagged paint colours |
| Popularity | How often other users choose the item for the same model, plus feedback |
| Price and availability | Retailer product feeds by country |
| Tutorials | YouTube Data API, ranked by relevance to the model |

**Cold start:** a new user has no history, but the app can still recommend using item attributes (the model's compatible parts, its typical paint scheme), so the first screen is useful without any personalization.

**Learning from feedback:** each recommendation has "Useful? Yes / Not for me" buttons. In the mockup they only change how the item looks. In the real app they would feed back into the scores.

## Screens

1. **Models:** browse by company (Games Workshop, Warlord Games) and choose a model. A short hint explains the three steps for a first-time user.
2. **My recommendations:** for the chosen model, with a region selector and an overseas toggle:
   - **Parts:** third-party bits (Kromlech, Archie's Forge), with match scores and warnings.
   - **Paints:** Games Workshop paints plus cheaper alternatives (Vallejo, Army Painter, AK Interactive, Pro Acryl) and the savings.
   - **Tools and brushes:** Games Workshop, Army Painter, Green Stuff World.
   - **Tutorials:** Open a YouTube search, in the YouTube app if it is installed (otherwise in the browser). The system Back gesture returns to Mini Companion.

## Run it

Requirements: Android Studio (built with Rabbit 2), Kotlin, Jetpack Compose, minimum SDK 26.

1. Clone the repo and open it in Android Studio.
2. Let Gradle sync finish.
3. Select an emulator (for example a Pixel) or a connected Android phone and press Run.

The tutorial buttons need the YouTube app or a web browser on the device.

## Project structure

```
app/src/main/java/.../
  MainActivity.kt   Compose UI: screens, cards, navigation
  Data.kt           Sample data: models, products, brands, regions
  Recommender.kt    Scoring, region filtering and ranking
app/src/main/res/drawable/
  ic_launcher_*.xml App icon
```

## Limitations of the mockup

- All product names, prices, availability and scores are illustrative sample data and may not match real products.
- Only four models and a handful of items per model are included.
- The scoring inputs are hardcoded, not learned. Nothing is stored between sessions.
- Feedback buttons are visual only.
- Tutorial buttons open a YouTube search, not a chosen video, and leave the app. In the full app these would be specific videos, ideally played inside Mini Companion..

## AI use

The mockup was created with the help of Claude (Anthropic), as the assignment requires.
