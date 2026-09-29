# Raster → SVG Vectorizer — Project Instructions

## Project summary

A Java command-line tool, built with **Maven**, that converts a raster image
(JPEG/PNG) into an SVG file composed of smooth cubic Bezier paths. The
pipeline is deliberately decomposed into single-responsibility stages, each
operating on a well-defined data representation, so that each stage can be
tested in isolation before the next is written.

**Runtime dependencies: standard library only** (`java.awt`, `javax.imageio`,
`java.util`, `java.io`, `java.nio`). Do not add Apache Commons Math, EJML,
image-processing libraries, or any other runtime dependency unless explicitly
asked — the algorithmic work is the whole point of the project. JUnit 5 for
**test scope** is expected and encouraged (see Testing below).

**Java 17+.** Records, sealed types, switch expressions, and pattern matching
are all fair game when they improve clarity.

## Build & Run

Standard Maven layout. Coordinates: `svgizer:svgizer:1.0-SNAPSHOT` unless told
otherwise. Java release = 17.

Recommended `pom.xml` shape (ask before deviating):

- `maven-compiler-plugin` — `<release>17</release>`
- `maven-surefire-plugin` — for JUnit 5 tests
- `maven-shade-plugin` (or `maven-assembly-plugin`) — build a runnable fat JAR
  with `Main-Class: svgizer.Main`
- `exec-maven-plugin` — for `mvn exec:java` during development
- `junit-jupiter` — test scope only

Common invocations:

mvn compile
mvn test
mvn exec:java -Dexec.args="in.jpg out.svg"
mvn package # produces target/svgizer-1.0-SNAPSHOT.jar
java -jar target/svgizer-1.0-SNAPSHOT.jar in.jpg out.svg


When suggesting commands, prefer Maven goals over raw `javac`/`java`.

## Architecture: the pipeline

The conversion is a linear pipeline. Each stage consumes the previous stage's
output and produces a new representation of the same underlying object. **Never
merge stages** — if you're tempted to combine e.g. threshold and tracing, don't.
Keep the boundaries crisp so each stage stays independently testable.


JPEG/PNG file
│
▼ ImageIO.read
BufferedImage (W × H, packed ARGB)
│
▼ Raster.toGray
float[W][H] in [0, 1] (luminance)
│
▼ Raster.gaussianBlur (optional, σ ≥ 0)
float[W][H] in [0, 1] (smoothed)
│
▼ threshold
boolean[W][H] (true = ink / foreground)
│
▼ ContourTracer.trace
List<double[][]> (dense closed polylines, pixel coords)
│
▼ Simplify.rdp
List<double[][]> (sparse closed polylines)
│
▼ BezierFitter.fit
List<List<Cubic>> (cubic Bezier chains per loop)
│
▼ SvgWriter.write
.svg file on disk


## Package layout

Root package: `svgizer`. Maven standard tree:

pom.xml
src/
main/java/svgizer/
Main.java
Raster.java
ContourTracer.java
Simplify.java
BezierFitter.java
SvgWriter.java
test/java/svgizer/
(mirror package — one test class per stage)


| File                  | Responsibility                                            |
|-----------------------|-----------------------------------------------------------|
| `Main.java`           | CLI parsing, orchestration, wiring stages together        |
| `Raster.java`         | Loading, grayscale conversion, Gaussian blur              |
| `ContourTracer.java`  | Marching-squares boundary tracing                         |
| `Simplify.java`       | Ramer–Douglas–Peucker polyline simplification             |
| `BezierFitter.java`   | Schneider curve fitting                                   |
| `SvgWriter.java`      | SVG serialization                                         |

Do **not** introduce a "Util" catch-all class. If a function doesn't clearly
belong to one of the stages above, it belongs in the stage that uses it.

## Data representation conventions

These are load-bearing. Violating them will cause subtle bugs and bad
suggestions. Follow them everywhere.

### Coordinate order
- All 2D arrays are indexed `[x][y]` — column-major, x outer, y inner.
- Rationale: marching squares iterates cells and reads more naturally with x
  on the outside. Pick this and never flip it.
- A "point" is a `double[]{x, y}` (length-2 array). Never `int[]`. Never a
  custom `Point` class unless there's a strong reason.

### Grayscale / intensity
- Normalized to `[0, 1]`. Never `[0, 255]`.
- Rec. 709 luma weights: `0.2126 R + 0.7152 G + 0.0722 B`.
- `float`, not `double`. Memory matters at image scale.

### Binary mask
- `boolean[W][H]`.
- `true` = foreground = ink (dark pixels).
- `false` = background = paper (light pixels).
- Do not add a separate "foreground color" concept. It's binary.

### Contour representation
- Each contour is a `double[][]` where element `i` is `{x, y}`.
- **Closed loop, last point NOT repeated.** The closure is implicit.
- Coordinates are in image pixel space (matches the original image's viewBox).

### SVG output
- All contours go into a **single** `<path>` element.
- Use `fill-rule="evenodd"` so nested loops automatically become holes.
- Do not compute contour orientation (CW vs CCW) — even-odd makes it irrelevant.

## Coding style

- **Small static methods.** Most stages are pure functions; keep them static
  and package-private unless a stage has genuine state.
- **`final` classes with private constructors** for pure-utility stages
  (e.g. `public final class Simplify { private Simplify() {} ... }`).
- **No comments that restate code.** Comment only the *why*: non-obvious
  invariants, coordinate conventions, algorithm references, mathematical
  reasoning. If you're tempted to write `// iterate over pixels`, don't.
- **Named constants over magic numbers.** `MAX_ITERATIONS`, `EPSILON`, etc.
- **`Locale.ROOT`** in every `String.format` / `PrintWriter.printf`. SVG
  coordinates must use `.` as the decimal separator regardless of system
  locale.
- **Streams are fine for collection transforms, avoided for hot loops.**
  Image iteration stays as plain `for`.

## Algorithmic conventions

### Marching squares (Stage 4)
- Treat **pixels as lattice corners**, not cells. A "cell" is the imaginary
  square between four adjacent pixels.
- **Pad the mask with a 1-pixel paper border** before tracing. This makes the
  "every crossing has in-degree 1 and out-degree 1" invariant hold at image
  borders. Subtract 1 from output coordinates to unpad.
- **Emission cycle:** BL → BR → TR → TL → BL. For each ink corner, emit a
  segment from the edge-midpoint shared with its *previous* corner to the
  edge-midpoint shared with its *next* corner.
- **Connectivity:** 8-connectivity for ink, 4-connectivity for paper. This is
  implicit in the emission rule; do not add extra logic to "resolve" saddle
  cases. Let global chaining handle them.
- **Doubled-integer coordinates for hashing.** Midpoints have half-integer
  coordinates; store them internally as `(2x, 2y)` so hashmap keys stay
  integral. Pack into a `long` as `((long)x << 32) | (y & 0xffffffffL)`.
- **Chaining** uses a `Map<Long, Long>` from start-key to end-key, then
  walks each loop until it returns to its starting point. Every point appears
  as a start exactly once.

### RDP simplification (Stage 5)
- Uses **perpendicular distance** from a point to the segment (not to the
  infinite line).
- For closed loops: temporarily append the start point to the end, simplify,
  then treat the result as closed again. Do not attempt the two-anchor split
  trick unless explicitly asked; it's an optimization for a different problem.
- Tolerance is in **pixel units** (same coordinate space as the input).

### Schneider curve fitting (Stage 6)
- Standard algorithm from Graphics Gems (1990). If you're unsure of a step,
  reference that paper's pseudocode rather than inventing a variant.
- Fixed structure: `fitCubic` recurses; each recursion either accepts a
  cubic, improves it via Newton–Raphson reparameterization, or splits at the
  worst-error point.
- **Tangent conventions:** leftTangent uses `P[i+1] - P[i]`; rightTangent uses
  `P[i-1] - P[i]`; centerTangent uses `P[i-1] - P[i+1]`. All normalized.
- **Fallback** when the least-squares solution gives non-positive handle
  lengths: use `|P3 - P0| / 3` for both handles (Wu/Barsky heuristic).
- Error tolerance is in **pixel units**, applied as `error * error` against
  squared distances to avoid `sqrt` in hot loops.

## Implementation order

When asked to implement or extend a stage, remember that stages must be built
and validated **in order**:

1. Load + grayscale + threshold (validate by printing the mask as ASCII art)
2. Marching squares (validate by dumping polylines as SVG `<path>`)
3. RDP (validate visually against step 2)
4. Bezier fitting (validate visually against step 3)
5. CLI / polish / multi-level thresholding (optional)

If asked to "jump ahead" to stage 4 or 6 before earlier stages work, push
back gently: the whole point of the pipeline is that each stage can be
checked against the last.

## Testing philosophy

JUnit 5 under `src/test/java`, one test class per stage, mirroring the main
package. Run with `mvn test`.

- **Micro-tests first.** Hand-craft tiny inputs (3×3, 4×4) and predict the
  exact output by hand. Marching squares on a single ink pixel should produce
  a 4-point diamond, not a square. Write this test before writing the tracer.
- **Visual tests second.** Serialize intermediate stages as SVG and open them
  in a browser. A rasterized contour with `stroke-width="0.5"` and
  `fill="none"` should sit exactly between ink and paper pixels.
- **Invariant checks as assertions.** After tracing, every contour should have
  ≥ 4 points and form a closed loop. Assert this in the test — and consider
  a cheap `assert` in the production code too.
- **Test names describe behavior, not implementation.** Prefer
  `singleInkPixelProducesFourPointDiamond` over `testTrace1`.
- **Fixture images** go in `src/test/resources/`, loaded via
  `getClass().getResourceAsStream(...)`. Do not read from absolute paths.

## Common pitfalls (avoid these)

- **Flipping coordinate order mid-pipeline.** `[x][y]` everywhere.
- **Returning closed loops with the first point duplicated at the end.**
  Closure is implicit. Do not repeat the start point.
- **Using floating-point map keys in the contour tracer.** Use doubled-int
  packed longs.
- **Trying to "resolve" saddle cases locally.** The emission rule is fixed;
  the global chaining handles it.
- **Computing contour winding/orientation for SVG fill.** Use `evenodd` and
  skip orientation entirely.
- **Mixing up `x` and `y` when converting to/from padded coordinates.**
  The padding adds 1 to *both* indices; unpadding subtracts 1 from both.
- **Using `String.format` without `Locale.ROOT`.** Decimal commas break SVG.
- **Forgetting the 1-pixel pad before tracing.** Without it, border contours
  come out open and the chaining map has dangling ends.
- **Reading pixels with `getRGB(x, y)` in a loop.** Use the bulk
  `getRGB(0, 0, w, h, null, 0, w)` form.
- **Adding a runtime dependency to `pom.xml` for something small.** If you
  think you need a library, ask first — the point of the project is to
  implement the algorithms.
- **Writing tests that read images from the CWD.** Use classpath resources
  under `src/test/resources/`.

## Domain glossary

- **Mask** — the `boolean[W][H]` produced by thresholding.
- **Cell** — the imaginary square between four adjacent pixels; the unit of
  marching squares.
- **Crossing** — a point on a cell edge where ink and paper meet; always at
  an edge midpoint.
- **Emission** — the act of adding one small directed segment to the tracer's
  output for one ink corner in one cell.
- **Chain** — a sequence of cubic Beziers whose endpoints match end-to-start.
- **Loop** — a closed contour, either an outer boundary or a hole.
- **Saddle** — a cell where the two ink corners are diagonal. Not a special
  case in code; a descriptive term only.

## When in doubt

- Prefer the standard published algorithm over an ad-hoc invention. Reference
  it in a comment when non-trivial.
- Prefer a clear two-line implementation over a clever one-liner.
- If a suggestion would require adding a **runtime** dependency, stop and ask.
- If a suggestion would merge two pipeline stages, stop and ask.
