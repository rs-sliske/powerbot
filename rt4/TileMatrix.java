package org.powerbot.script.rt4;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.script.InteractiveEntity;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;

/**
 * TileMatrix
 * An interactive tile matrix.
 */
public final class TileMatrix extends Interactive implements InteractiveEntity {
	public static final Color TARGET_COLOR = new Color(255, 0, 0, 75);
	private final Tile tile;

	public TileMatrix(final ClientContext ctx, final Tile tile) {
		super(ctx);
		this.tile = tile;
	}

	@Override
	public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		boundingModel.set(new BoundingModel(ctx, x1, x2, y1, y2, z1, z2) {
			@Override
			public int x() {
				final Tile base = ctx.game.mapOffset();
				return ((tile.x() - base.x()) * 128) + 64;
			}

			@Override
			public int z() {
				final Tile base = ctx.game.mapOffset();
				return ((tile.y() - base.y()) * 128) + 64;
			}
		});
	}

	public Point point(final int height) {
		return point(0.5d, 0.5d, height);
	}

	public Point point(final double modX, final double modY, final int height) {
		final Tile base = ctx.game.mapOffset();
		return base != null ? ctx.game.worldToScreen((int) ((tile.x() - base.x() + modX) * 128d), (int) ((tile.y() - base.y() + modY) * 128d), height) : new Point(-1, -1);
	}

	@Deprecated
	public Polygon getBounds() {
		return bounds();
	}

	public Polygon bounds() {
		final Point tl = point(0.0D, 0.0D, 0);
		final Point tr = point(1.0D, 0.0D, 0);
		final Point br = point(1.0D, 1.0D, 0);
		final Point bl = point(0.0D, 1.0D, 0);
		return new Polygon(
				new int[]{tl.x, tr.x, br.x, bl.x},
				new int[]{tl.y, tr.y, br.y, bl.y},
				4
		);
	}

	@Override
	public Polygon[] triangles() {
		final BoundingModel m = boundingModel.get();
		if (m != null) {
			return m.triangles();
		}
		return new Polygon[]{
				bounds()
		};
	}

	public Point mapPoint() {
		return ctx.game.tileToMap(tile);
	}

	public boolean onMap() {
		final boolean r = ctx.game.resizable();
		final Point centre = r ? ctx.widgets.component(161, 17).centerPoint() : ctx.widgets.component(548, 6).centerPoint();
		final Point p = mapPoint();

		final double d = Math.pow(centre.x - p.x, 2) + Math.pow(centre.y - p.y, 2);
		if (r) {
			return d < Math.pow(68, 2);
		}
		return p.y < 70 ? d < Math.pow(68, 2) : p.y < 110 ? d < Math.pow(64, 2) : p.y < 135 ? d < Math.pow(52, 2) :
				Math.pow(centre.x - p.x, 2) + Math.pow(centre.y + 54 - p.y, 2) < Math.pow(16, 2);
	}

	public boolean reachable() {
		return ctx.movement.reachable(ctx.players.local().tile(), tile);
	}

	@Override
	public Tile tile() {
		return tile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inViewport() {
		return isPolygonInViewport(bounds());
	}

	private boolean isPolygonInViewport(final Polygon p) {
		for (int i = 0; i < p.npoints; i++) {
			if (!ctx.game.pointInViewport(p.xpoints[i], p.ypoints[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Point nextPoint() {
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.nextPoint();
		}
		if (!inViewport()) {
			return new Point(-1, -1);
		}
		final int x = Random.nextGaussian(0, 100, 5);
		final int y = Random.nextGaussian(0, 100, 5);
		return point(x / 100.0D, y / 100.0D, 0);
	}

	@Override
	public Point centerPoint() {
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.centerPoint();
		}
		if (!inViewport()) {
			return new Point(-1, -1);
		}
		return point(0);
	}

	@Override
	public boolean contains(final Point point) {
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.contains(point);
		}
		final Polygon p = bounds();
		return isPolygonInViewport(p) && p.contains(point);
	}

	@Override
	public boolean valid() {
		final Tile t = ctx.game.mapOffset();
		if (t == null) {
			return false;
		}
		final int x = tile.x() - t.x(), y = tile.y() - t.y();
		return x >= 0 && y >= 0 && x < 104 && y < 104;
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof TileMatrix && ((TileMatrix) o).tile.equals(tile);
	}

	@Override
	public int hashCode() {
		return tile.hashCode();
	}

	@Override
	public String toString() {
		return tile.toString();
	}
}
