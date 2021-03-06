/* 
 * Timemap.js Copyright 2008 Nick Rabinowitz.
 * Licensed under the MIT License (see LICENSE.txt)
 */
var DT = Timeline.DateTime, GIP = "http://www.google.com/intl/en_us/mapfiles/ms/icons/";
function TimeMap(d, g, l) {
	this.mElement = g;
	this.tElement = d;
	this.datasets = {};
	this.filters = {};
	this.mapBounds = new GLatLngBounds();
	this.opts = l || {};
	if (typeof (l.mapType) == "string") {
		l.mapType = TimeMap.mapTypes[l.mapType]
	}
	if (typeof (l.mapFilter) == "string") {
		l.mapFilter = TimeMap.filters[l.mapFilter]
	}
	var j = l.mapCenter || new GLatLng(0, 0), h = l.mapZoom || 0, b = l.mapType
			|| G_PHYSICAL_MAP, k = l.mapTypes
			|| [ G_NORMAL_MAP, G_SATELLITE_MAP, G_PHYSICAL_MAP ], c = ("showMapTypeCtrl" in l) ? l.showMapTypeCtrl
			: true, f = ("showMapCtrl" in l) ? l.showMapCtrl : true;
	this.opts.syncBands = ("syncBands" in l) ? l.syncBands : true;
	this.opts.mapFilter = l.mapFilter || TimeMap.filters.hidePastFuture;
	this.opts.centerOnItems = ("centerMapOnItems" in l) ? l.centerMapOnItems
			: true;
	this.opts.theme = TimeMapTheme.create(l.theme, l);
	if (GBrowserIsCompatible()) {
		this.map = new GMap2(this.mElement);
		var a = this.map;
		if (f) {
			a.addControl(new GLargeMapControl())
		}
		if (c) {
			a.addControl(new GMapTypeControl())
		}
		var e;
		for (e = G_DEFAULT_MAP_TYPES.length - 1; e > 0; e--) {
			a.removeMapType(G_DEFAULT_MAP_TYPES[e])
		}
		a.addMapType(k[0]);
		a.removeMapType(G_DEFAULT_MAP_TYPES[0]);
		for (e = 1; e < k.length; e++) {
			a.addMapType(k[e])
		}
		a.enableDoubleClickZoom();
		a.enableScrollWheelZoom();
		a.enableContinuousZoom();
		a.setCenter(j, h);
		a.setMapType(b)
	}
}
TimeMap.version = "1.5";
TimeMap.init = function(c) {
	if (!("mapId" in c) || !c.mapId) {
		throw "TimeMap.init: No id for map"
	}
	if (!("timelineId" in c) || !c.timelineId) {
		throw "TimeMap.init: No id for timeline"
	}
	c = c || {};
	c.options = c.options || {};
	c.datasets = c.datasets || [];
	c.bandInfo = c.bandInfo || false;
	c.scrollTo = c.scrollTo || "earliest";
	if (!c.bandInfo && !c.bands) {
		var i = c.bandIntervals || c.options.bandIntervals
				|| [ DT.WEEK, DT.MONTH ];
		if (typeof (i) == "string") {
			i = TimeMap.intervals[i]
		}
		c.options.bandIntervals = i;
		c.bandInfo = [ {
			width : "80%",
			intervalUnit : i[0],
			intervalPixels : 70
		}, {
			width : "20%",
			intervalUnit : i[1],
			intervalPixels : 100,
			showEventText : false,
			overview : true,
			trackHeight : 0.4,
			trackGap : 0.2
		} ]
	}
	var l = new TimeMap(document.getElementById(c.timelineId), document
			.getElementById(c.mapId), c.options);
	var g = [], k, b, e, a;
	for (k = 0; k < c.datasets.length; k++) {
		b = c.datasets[k];
		e = b.options || {};
		e.title = b.title || "";
		e.theme = b.theme;
		e.dateParser = b.dateParser;
		a = b.id || "ds" + k;
		g[k] = l.createDataset(a, e);
		if (k > 0) {
			g[k].eventSource = g[0].eventSource
		}
	}
	l.eventSource = g[0].eventSource;
	var f = [];
	var d = (g[0] && g[0].eventSource) || new Timeline.DefaultEventSource();
	if (c.bands) {
		f = c.bands;
		for (k = 0; k < f.length; k++) {
			if (f[k].eventSource !== null) {
				f[k].eventSource = d
			}
		}
	} else {
		for (k = 0; k < c.bandInfo.length; k++) {
			var j = c.bandInfo[k];
			if (!(("eventSource" in j) && !j.eventSource)) {
				j.eventSource = d
			} else {
				j.eventSource = null
			}
			f[k] = Timeline.createBandInfo(j);
			if (k > 0 && TimeMap.util.TimelineVersion() == "1.2") {
				f[k].eventPainter.setLayout(f[0].eventPainter.getLayout())
			}
		}
	}
	l.initTimeline(f);
	var h = TimeMap.loadManager;
	h.init(l, c.datasets.length, c);
	for (k = 0; k < c.datasets.length; k++) {
		(function(n) {
			var r = c.datasets[n], o, q, s, p, m;
			o = r.options || r.data || {};
			q = r.type || o.type;
			s = function() {
				h.increment()
			};
			p = (typeof (q) == "string") ? TimeMap.loaders[q] : q;
			m = new p(o);
			m.load(g[n], s)
		})(k)
	}
	return l
};
var timemapInit = TimeMap.init;
TimeMap.loadManager = new function() {
	this.init = function(a, c, b) {
		this.count = 0;
		this.tm = a;
		this.target = c;
		this.opts = b || {}
	};
	this.increment = function() {
		this.count++;
		if (this.count >= this.target) {
			this.complete()
		}
	};
	this.complete = function() {
		var a = this.tm;
		var c = this.opts.dataLoadedFunction;
		if (c) {
			c(a)
		} else {
			var f = new Date();
			var e = this.tm.eventSource;
			var b = this.opts.scrollTo;
			if (b && e.getCount() > 0) {
				switch (b) {
				case "now":
					break;
				case "earliest":
					f = e.getEarliestDate();
					break;
				case "latest":
					f = e.getLatestDate();
					break;
				default:
					if (typeof (b) == "string") {
						b = TimeMapDataset.hybridParser(b)
					}
					if (b.constructor == Date) {
						f = b
					}
				}
				this.tm.timeline.getBand(0).setCenterVisibleDate(f)
			}
			this.tm.timeline.layout();
			c = this.opts.dataDisplayedFunction;
			if (c) {
				c(a)
			}
		}
	}
};
TimeMap.loaders = {};
TimeMap.loaders.basic = function(a) {
	TimeMap.loaders.mixin(this, a);
	this.data = a.items || a.value || []
};
TimeMap.loaders.basic.prototype.load = function(b, c) {
	var a = this.preload(this.data);
	b.loadItems(a, this.transform);
	c()
};
TimeMap.loaders.remote = function(a) {
	TimeMap.loaders.mixin(this, a);
	this.url = a.url
};
TimeMap.loaders.remote.prototype.load = function(b, c) {
	var a = this;
	GDownloadUrl(this.url, function(d) {
		var e = a.parse(d);
		e = a.preload(e);
		b.loadItems(e, a.transform);
		c()
	})
};
TimeMap.loaders.mixin = function(a, b) {
	var c = function(d) {
		return d
	};
	a.parse = b.parserFunction || c;
	a.preload = b.preloadFunction || c;
	a.transform = b.transformFunction || c
};
TimeMap.intervals = {
	sec : [ DT.SECOND, DT.MINUTE ],
	min : [ DT.MINUTE, DT.HOUR ],
	hr : [ DT.HOUR, DT.DAY ],
	day : [ DT.DAY, DT.WEEK ],
	wk : [ DT.WEEK, DT.MONTH ],
	mon : [ DT.MONTH, DT.YEAR ],
	yr : [ DT.YEAR, DT.DECADE ],
	dec : [ DT.DECADE, DT.CENTURY ]
};
TimeMap.mapTypes = {
	normal : G_NORMAL_MAP,
	satellite : G_SATELLITE_MAP,
	hybrid : G_HYBRID_MAP,
	physical : G_PHYSICAL_MAP,
	moon : G_MOON_VISIBLE_MAP,
	sky : G_SKY_VISIBLE_MAP
};
TimeMap.prototype.createDataset = function(d, b) {
	b = b || {};
	if (!("title" in b)) {
		b.title = d
	}
	var c = new TimeMapDataset(this, b);
	this.datasets[d] = c;
	if (this.opts.centerOnItems) {
		var a = this;
		GEvent.addListener(c, "itemsloaded", function() {
			var f = a.map, e = a.mapBounds;
			f.setZoom(f.getBoundsZoomLevel(e));
			f.setCenter(e.getCenter())
		})
	}
	return c
};
TimeMap.prototype.each = function(a) {
	for ( var b in this.datasets) {
		if (this.datasets.hasOwnProperty(b)) {
			a(this.datasets[b])
		}
	}
};
TimeMap.prototype.initTimeline = function(e) {
	for ( var a = 1; a < e.length; a++) {
		if (this.opts.syncBands) {
			e[a].syncWith = (a - 1)
		}
		e[a].highlight = true
	}
	this.timeline = Timeline.create(this.tElement, e);
	var c = this;
	this.timeline.getBand(0).addOnScrollListener(function() {
		c.filter("map")
	});
	var b = this.timeline.getBand(0).getEventPainter().constructor;
	b.prototype._showBubble = function(g, i, h) {
		h.item.openInfoWindow()
	};
	this.addFilterChain("map", function(g) {
		g.showPlacemark()
	}, function(g) {
		g.hidePlacemark()
	});
	this.addFilter("map", function(g) {
		return g.visible
	});
	this.addFilter("map", function(g) {
		return g.dataset.visible
	});
	this.addFilter("map", this.opts.mapFilter);
	this.addFilterChain("timeline", function(g) {
		g.showEvent()
	}, function(g) {
		g.hideEvent()
	});
	this.addFilter("timeline", function(g) {
		return g.visible
	});
	this.addFilter("timeline", function(g) {
		return g.dataset.visible
	});
	var f = null;
	var d = this.timeline;
	window.onresize = function() {
		if (f === null) {
			f = window.setTimeout(function() {
				f = null;
				d.layout()
			}, 500)
		}
	}
};
TimeMap.prototype.filter = function(b) {
	var a = this.filters[b];
	if (!a || !a.chain || a.chain.length === 0) {
		return
	}
	this.each(function(c) {
		c.each(function(f) {
			var d = false;
			F_LOOP: while (!d) {
				for ( var e = a.chain.length - 1; e >= 0; e--) {
					if (!a.chain[e](f)) {
						a.off(f);
						break F_LOOP
					}
				}
				a.on(f);
				d = true
			}
		})
	})
};
TimeMap.prototype.addFilterChain = function(c, b, a) {
	this.filters[c] = {
		chain : [],
		on : b,
		off : a
	}
};
TimeMap.prototype.removeFilterChain = function(a) {
	this.filters[a] = null
};
TimeMap.prototype.addFilter = function(b, a) {
	if (this.filters[b] && this.filters[b].chain) {
		this.filters[b].chain.push(a)
	}
};
TimeMap.prototype.removeFilter = function(a) {
	if (this.filters[a] && this.filters[a].chain) {
		this.filters[a].chain.pop()
	}
};
TimeMap.filters = {};
TimeMap.filters.hidePastFuture = function(d) {
	var f = d.dataset.timemap.timeline.getBand(0);
	var b = f.getMaxVisibleDate().getTime();
	var c = f.getMinVisibleDate().getTime();
	if (d.event !== null) {
		var a = d.event.getStart().getTime();
		var e = d.event.getEnd().getTime();
		if (a > b) {
			return false
		} else {
			if (e < c || (d.event.isInstant() && a < c)) {
				return false
			}
		}
	}
	return true
};
TimeMap.filters.showMomentOnly = function(b) {
	var d = b.dataset.timemap.timeline.getBand(0);
	var e = d.getCenterVisibleDate().getTime();
	if (b.event !== null) {
		var a = b.event.getStart().getTime();
		var c = b.event.getEnd().getTime();
		if (a > e) {
			return false
		} else {
			if (c < e || (b.event.isInstant() && a < e)) {
				return false
			}
		}
	}
	return true
};
function TimeMapDataset(a, b) {
	this.timemap = a;
	this.eventSource = new Timeline.DefaultEventSource();
	this.items = [];
	this.visible = true;
	this.opts = b || {};
	this.opts.title = b.title || "";
	var d = this.timemap.opts.theme, c = b.theme || d;
	b.eventIconPath = b.eventIconPath || d.eventIconPath;
	this.opts.theme = TimeMapTheme.create(c, b);
	if (typeof (b.dateParser) == "string") {
		b.dateParser = TimeMapDataset.dateParsers[b.dateParser]
	}
	this.opts.dateParser = b.dateParser || TimeMapDataset.hybridParser;
	this.getItems = function(e) {
		if (e !== undefined) {
			if (e < this.items.length) {
				return this.items[e]
			} else {
				return null
			}
		}
		return this.items
	};
	this.getTitle = function() {
		return this.opts.title
	}
}
TimeMapDataset.gregorianParser = function(b) {
	if (!b) {
		return null
	} else {
		if (b instanceof Date) {
			return b
		}
	}
	var c = Boolean(b.match(/b\.?c\.?/i));
	var a = parseInt(b);
	if (!isNaN(a)) {
		if (c) {
			a = 1 - a
		}
		var e = new Date(0);
		e.setUTCFullYear(a);
		return e
	} else {
		return null
	}
};
TimeMapDataset.hybridParser = function(a) {
	var c = new Date(Date.parse(a));
	if (isNaN(c)) {
		if (a.match(/^-?\d{1,6} ?(a\.?d\.?|b\.?c\.?e?\.?|c\.?e\.?)?$/i)) {
			c = TimeMapDataset.gregorianParser(a)
		} else {
			try {
				c = DT.parseIso8601DateTime(a)
			} catch (b) {
				c = null
			}
		}
	}
	return c
};
TimeMapDataset.dateParsers = {
	hybrid : TimeMapDataset.hybridParser,
	iso8601 : DT.parseIso8601DateTime,
	gregorian : TimeMapDataset.gregorianParser
};
TimeMapDataset.prototype.each = function(b) {
	for ( var a = 0; a < this.items.length; a++) {
		b(this.items[a])
	}
};
TimeMapDataset.prototype.loadItems = function(c, b) {
	for ( var a = 0; a < c.length; a++) {
		this.loadItem(c[a], b)
	}
	GEvent.trigger(this, "itemsloaded")
};
TimeMapDataset.prototype.loadItem = function(B, r) {
	if (r !== undefined) {
		B = r(B)
	}
	if (B === null) {
		return
	}
	var j = B.options || {}, g = this.timemap, c = this.opts.theme, A = j.theme
			|| c;
	j.eventIconPath = j.eventIconPath || c.eventIconPath;
	A = TimeMapTheme.create(A, j);
	var f = this.opts.dateParser, m = B.start, k = B.end, e;
	m = (m === undefined || m === "") ? null : f(m);
	k = (k === undefined || k === "") ? null : f(k);
	e = (k === undefined);
	var d = A.eventIcon, C = B.title, w = null;
	if (m !== null) {
		var o = Timeline.DefaultEventSource.Event;
		if (TimeMap.util.TimelineVersion() == "1.2") {
			w = new o(m, k, null, null, e, C, null, null, null, d,
					A.eventColor, A.eventTextColor)
		} else {
			var l = A.eventTextColor;
			if (!l) {
				l = (A.classicTape && !e) ? "#FFFFFF" : "#000000"
			}
			w = new o( {
				start : m,
				end : k,
				instant : e,
				text : C,
				icon : d,
				color : A.eventColor,
				textColor : l
			})
		}
	}
	var a = A.icon, n = g.mapBounds;
	var t = function(E) {
		var D = null, F = "", H = null;
		if ("point" in E) {
			H = new GLatLng(parseFloat(E.point.lat), parseFloat(E.point.lon));
			if (g.opts.centerOnItems) {
				n.extend(H)
			}
			D = new GMarker(H, {
				icon : a
			});
			F = "marker";
			H = D.getLatLng()
		} else {
			if ("polyline" in E || "polygon" in E) {
				var J = [], K;
				if ("polyline" in E) {
					K = E.polyline
				} else {
					K = E.polygon
				}
				for ( var G = 0; G < K.length; G++) {
					H = new GLatLng(parseFloat(K[G].lat), parseFloat(K[G].lon));
					J.push(H);
					if (g.opts.centerOnItems) {
						n.extend(H)
					}
				}
				if ("polyline" in E) {
					D = new GPolyline(J, A.lineColor, A.lineWeight,
							A.lineOpacity);
					F = "polyline";
					H = D.getVertex(Math.floor(D.getVertexCount() / 2))
				} else {
					D = new GPolygon(J, A.polygonLineColor,
							A.polygonLineWeight, A.polygonLineOpacity,
							A.fillColor, A.fillOpacity);
					F = "polygon";
					H = D.getBounds().getCenter()
				}
			} else {
				if ("overlay" in E) {
					var I = new GLatLng(parseFloat(E.overlay.south),
							parseFloat(E.overlay.west));
					var p = new GLatLng(parseFloat(E.overlay.north),
							parseFloat(E.overlay.east));
					if (g.opts.centerOnItems) {
						n.extend(I);
						n.extend(p)
					}
					var i = new GLatLngBounds(I, p);
					D = new GGroundOverlay(E.overlay.image, i);
					F = "overlay";
					H = i.getCenter()
				}
			}
		}
		return {
			placemark : D,
			type : F,
			point : H
		}
	};
	var z = [], u = [], b = null, h = "", v = null, x;
	if ("placemarks" in B) {
		u = B.placemarks
	} else {
		var q = [ "point", "polyline", "polygon", "overlay" ];
		for (x = 0; x < q.length; x++) {
			if (q[x] in B) {
				b = {};
				b[q[x]] = B[q[x]];
				u.push(b)
			}
		}
	}
	if (u) {
		for (x = 0; x < u.length; x++) {
			var s = t(u[x]);
			v = v || s.point;
			h = h || s.type;
			z.push(s.placemark)
		}
	}
	if (z.length > 1) {
		h = "array"
	}
	j.title = C;
	j.type = h || "none";
	j.theme = A;
	if (j.infoPoint) {
		j.infoPoint = new GLatLng(parseFloat(j.infoPoint.lat),
				parseFloat(j.infoPoint.lon))
	} else {
		j.infoPoint = v
	}
	var y = new TimeMapItem(z, w, this, j);
	if (w !== null) {
		w.item = y;
		this.eventSource.add(w)
	}
	if (z.length > 0) {
		for (x = 0; x < z.length; x++) {
			z[x].item = y;
			GEvent.addListener(z[x], "click", function() {
				y.openInfoWindow()
			});
			g.map.addOverlay(z[x]);
			z[x].hide()
		}
	}
	this.items.push(y);
	return y
};
function TimeMapTheme(b) {
	b = b || {};
	if (!b.icon) {
		var a = new GIcon(G_DEFAULT_ICON);
		this.iconImage = b.iconImage || GIP + "red-dot.png";
		a.image = this.iconImage;
		a.iconSize = new GSize(32, 32);
		a.shadow = GIP + "msmarker.shadow.png";
		a.shadowSize = new GSize(59, 32);
		a.iconAnchor = new GPoint(16, 33);
		a.infoWindowAnchor = new GPoint(18, 3)
	}
	this.icon = b.icon || a;
	this.color = b.color || "#FE766A";
	this.lineColor = b.lineColor || this.color;
	this.polygonLineColor = b.polygonLineColor || this.lineColor;
	this.lineOpacity = b.lineOpacity || 1;
	this.polgonLineOpacity = b.polgonLineOpacity || this.lineOpacity;
	this.lineWeight = b.lineWeight || 2;
	this.polygonLineWeight = b.polygonLineWeight || this.lineWeight;
	this.fillColor = b.fillColor || this.color;
	this.fillOpacity = b.fillOpacity || 0.25;
	this.eventColor = b.eventColor || this.color;
	this.eventTextColor = b.eventTextColor || null;
	this.eventIconPath = b.eventIconPath || "timemap/images/";
	this.eventIconImage = b.eventIconImage || "red-circle.png";
	this.eventIcon = b.eventIcon || this.eventIconPath + this.eventIconImage;
	this.classicTape = ("classicTape" in b) ? b.classicTape : false
}
TimeMapTheme.create = function(b, a) {
	if (typeof (b) == "string") {
		if (b in TimeMap.themes) {
			return new TimeMap.themes[b](a)
		} else {
			b = null
		}
	}
	if (!b) {
		return new TimeMapTheme(a)
	}
	var d = new TimeMapTheme(), c;
	for (c in b) {
		if (b.hasOwnProperty(c)) {
			d[c] = a[c] || b[c];
			if (c == "eventIconPath") {
				d.eventIconImage = d[c] + b.eventIconImage
			}
		}
	}
	return d
};
TimeMap.themes = {
	red : function(a) {
		return new TimeMapTheme(a)
	},
	blue : function(a) {
		a = a || {};
		a.iconImage = GIP + "blue-dot.png";
		a.color = "#5A7ACF";
		a.eventIconImage = "blue-circle.png";
		return new TimeMapTheme(a)
	},
	green : function(a) {
		a = a || {};
		a.iconImage = GIP + "green-dot.png";
		a.color = "#19CF54";
		a.eventIconImage = "green-circle.png";
		return new TimeMapTheme(a)
	},
	ltblue : function(a) {
		a = a || {};
		a.iconImage = GIP + "ltblue-dot.png";
		a.color = "#5ACFCF";
		a.eventIconImage = "ltblue-circle.png";
		return new TimeMapTheme(a)
	},
	purple : function(a) {
		a = a || {};
		a.iconImage = GIP + "purple-dot.png";
		a.color = "#8E67FD";
		a.eventIconImage = "purple-circle.png";
		return new TimeMapTheme(a)
	},
	orange : function(a) {
		a = a || {};
		a.iconImage = GIP + "orange-dot.png";
		a.color = "#FF9900";
		a.eventIconImage = "orange-circle.png";
		return new TimeMapTheme(a)
	},
	yellow : function(a) {
		a = a || {};
		a.iconImage = GIP + "yellow-dot.png";
		a.color = "#ECE64A";
		a.eventIconImage = "yellow-circle.png";
		return new TimeMapTheme(a)
	}
};
function TimeMapItem(d, e, g, c) {
	this.event = e;
	this.dataset = g;
	this.map = g.timemap.map;
	if (d && TimeMap.util.isArray(d) && d.length === 0) {
		d = null
	}
	if (d && d.length == 1) {
		d = d[0]
	}
	this.placemark = d;
	this.opts = c || {};
	this.opts.type = c.type || "";
	this.opts.title = c.title || "";
	this.opts.description = c.description || "";
	this.opts.infoPoint = c.infoPoint || null;
	this.opts.infoHtml = c.infoHtml || "";
	this.opts.infoUrl = c.infoUrl || "";
	this.getType = function() {
		return this.opts.type
	};
	this.getTitle = function() {
		return this.opts.title
	};
	this.getInfoPoint = function() {
		return this.opts.infoPoint || this.map.getCenter()
	};
	this.visible = true;
	this.placemarkVisible = false;
	this.eventVisible = true;
	var b, a = g.opts, f = g.timemap.opts;
	b = c.openInfoWindow || a.openInfoWindow || f.openInfoWindow || false;
	if (!b) {
		if (this.opts.infoUrl !== "") {
			b = TimeMapItem.openInfoWindowAjax
		} else {
			b = TimeMapItem.openInfoWindowBasic
		}
	}
	this.openInfoWindow = b;
	this.closeInfoWindow = c.closeInfoWindow || a.closeInfoWindow
			|| f.closeInfoWindow || TimeMapItem.closeInfoWindowBasic
}
TimeMapItem.prototype.showPlacemark = function() {
	if (this.placemark) {
		if (this.getType() == "array") {
			for ( var a = 0; a < this.placemark.length; a++) {
				this.placemark[a].show()
			}
		} else {
			this.placemark.show()
		}
		this.placemarkVisible = true
	}
};
TimeMapItem.prototype.hidePlacemark = function() {
	if (this.placemark) {
		if (this.getType() == "array") {
			for ( var a = 0; a < this.placemark.length; a++) {
				this.placemark[a].hide()
			}
		} else {
			this.placemark.hide()
		}
		this.placemarkVisible = false
	}
	this.closeInfoWindow()
};
TimeMapItem.prototype.showEvent = function() {
	if (this.event) {
		if (this.eventVisible === false) {
			this.dataset.timemap.timeline.getBand(0).getEventSource()._events._events
					.add(this.event)
		}
		this.eventVisible = true
	}
};
TimeMapItem.prototype.hideEvent = function() {
	if (this.event) {
		if (this.eventVisible == true) {
			this.dataset.timemap.timeline.getBand(0).getEventSource()._events._events
					.remove(this.event)
		}
		this.eventVisible = false
	}
};
TimeMapItem.openInfoWindowBasic = function() {
	var a = this.opts.infoHtml;
	if (a === "") {
		a = '<div class="infotitle">' + this.opts.title + "</div>";
		if (this.opts.description !== "") {
			a += '<div class="infodescription">' + this.opts.description
					+ "</div>"
		}
	}
	if (this.placemark && !this.visible && this.event) {
		var b = this.dataset.timemap.timeline.getBand(0);
		b.setCenterVisibleDate(this.event.getStart())
	}
	if (this.getType() == "marker") {
		this.placemark.openInfoWindowHtml(a)
	} else {
		this.map.openInfoWindowHtml(this.getInfoPoint(), a)
	}
	this.selected = true
};
TimeMapItem.openInfoWindowAjax = function() {
	if (this.opts.infoHtml !== "") {
		this.openInfoWindow = TimeMapItem.openInfoWindowBasic;
		this.openInfoWindow()
	} else {
		if (this.opts.infoUrl !== "") {
			var a = this;
			GDownloadUrl(this.opts.infoUrl, function(b) {
				a.opts.infoHtml = b;
				a.openInfoWindow()
			})
		} else {
			this.openInfoWindow = TimeMapItem.openInfoWindowBasic;
			this.openInfoWindow()
		}
	}
};
TimeMapItem.closeInfoWindowBasic = function() {
	if (this.getType() == "marker") {
		this.placemark.closeInfoWindow()
	} else {
		var a = this.map.getInfoWindow();
		if (a.getPoint() == this.getInfoPoint() && !a.isHidden()) {
			this.map.closeInfoWindow()
		}
	}
	this.selected = false
};
TimeMap.util = {};
TimeMap.util.trim = function(a) {
	a = a && String(a) || "";
	return a.replace(/^\s\s*/, "").replace(/\s\s*$/, "")
};
TimeMap.util.isArray = function(a) {
	return a && !(a.propertyIsEnumerable("length")) && typeof a === "object"
			&& typeof a.length === "number"
};
TimeMap.util.getTagValue = function(e, a, c) {
	var d = "";
	var b = TimeMap.util.getNodeList(e, a, c);
	if (b.length > 0) {
		e = b[0].firstChild;
		while (e !== null) {
			d += e.nodeValue;
			e = e.nextSibling
		}
	}
	return d
};
TimeMap.util.nsMap = {};
TimeMap.util.getNodeList = function(d, b, c) {
	var a = TimeMap.util.nsMap;
	if (c === undefined) {
		return d.getElementsByTagName(b)
	}
	if (d.getElementsByTagNameNS && a[c]) {
		return d.getElementsByTagNameNS(a[c], b)
	}
	return d.getElementsByTagName(c + ":" + b)
};
TimeMap.util.makePoint = function(c, d) {
	var b = null, a = TimeMap.util.trim;
	if (c.lat && c.lng) {
		b = [ c.lat(), c.lng() ]
	}
	if (TimeMap.util.isArray(c)) {
		b = c
	}
	if (b === null) {
		c = a(c);
		if (c.indexOf(",") > -1) {
			b = c.split(",")
		} else {
			b = c.split(/[\r\n\f ]+/)
		}
	}
	if (b.length > 2) {
		b = b.slice(0, 2)
	}
	if (d) {
		b.reverse()
	}
	return {
		lat : a(b[0]),
		lon : a(b[1])
	}
};
TimeMap.util.makePoly = function(d, f) {
	var c = [], b;
	var e = TimeMap.util.trim(d).split(/[\r\n\f ]+/);
	if (e.length == 0) {
		return []
	}
	for ( var a = 0; a < e.length; a++) {
		b = (e[a].indexOf(",") > 0) ? e[a].split(",") : [ e[a], e[++a] ];
		if (b.length > 2) {
			b = b.slice(0, 2)
		}
		if (f) {
			b.reverse()
		}
		c.push( {
			lat : b[0],
			lon : b[1]
		})
	}
	return c
};
TimeMap.util.formatDate = function(h, g) {
	g = g || 3;
	var i = "";
	if (h) {
		if (h.toISOString) {
			return h.toISOString()
		}
		var a = function(d) {
			return ((d < 10) ? "0" : "") + d
		};
		var f = h.getUTCFullYear(), b = h.getUTCMonth(), j = h.getUTCDate();
		i += f + "-" + a(b + 1) + "-" + a(j);
		if (g > 1) {
			var c = h.getUTCHours(), e = h.getUTCMinutes(), k = h
					.getUTCSeconds();
			i += "T" + a(c) + ":" + a(e);
			if (g > 2) {
				i += a(k)
			}
			i += "Z"
		}
	}
	return i
};
TimeMap.util.TimelineVersion = function() {
	if (Timeline.version) {
		return Timeline.version
	}
	if (Timeline.DurationEventPainter) {
		return "1.2"
	} else {
		return "2.2.0"
	}
};
TimeMap.util.getPlacemarkType = function(a) {
	if ("getIcon" in a) {
		return "marker"
	}
	if ("getVertex" in a) {
		return "setFillStyle" in a ? "polygon" : "polyline"
	}
	return false
};
if (!this.JSON) {
	JSON = {}
}
(function() {
	function f(n) {
		return n < 10 ? "0" + n : n
	}
	if (typeof Date.prototype.toJSON !== "function") {
		Date.prototype.toJSON = function(key) {
			return this.getUTCFullYear() + "-" + f(this.getUTCMonth() + 1)
					+ "-" + f(this.getUTCDate()) + "T" + f(this.getUTCHours())
					+ ":" + f(this.getUTCMinutes()) + ":"
					+ f(this.getUTCSeconds()) + "Z"
		};
		String.prototype.toJSON = Number.prototype.toJSON = Boolean.prototype.toJSON = function(
				key) {
			return this.valueOf()
		}
	}
	var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g, escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g, gap, indent, meta = {
		"\b" : "\\b",
		"\t" : "\\t",
		"\n" : "\\n",
		"\f" : "\\f",
		"\r" : "\\r",
		'"' : '\\"',
		"\\" : "\\\\"
	}, rep;
	function quote(string) {
		escapable.lastIndex = 0;
		return escapable.test(string) ? '"' + string.replace(escapable,
				function(a) {
					var c = meta[a];
					if (typeof c === "string") {
						return c
					}
					return "\\u"
							+ ("0000" + a.charCodeAt(0).toString(16)).slice(-4)
				}) + '"' : '"' + string + '"'
	}
	function str(key, holder) {
		var i, k, v, length, mind = gap, partial, value = holder[key];
		if (value && typeof value === "object"
				&& typeof value.toJSON === "function") {
			value = value.toJSON(key)
		}
		if (typeof rep === "function") {
			value = rep.call(holder, key, value)
		}
		switch (typeof value) {
		case "string":
			return quote(value);
		case "number":
			return isFinite(value) ? String(value) : "null";
		case "boolean":
		case "null":
			return String(value);
		case "object":
			if (!value) {
				return "null"
			}
			gap += indent;
			partial = [];
			if (typeof value.length === "number"
					&& !value.propertyIsEnumerable("length")) {
				length = value.length;
				for (i = 0; i < length; i += 1) {
					partial[i] = str(i, value) || "null"
				}
				v = partial.length === 0 ? "[]" : gap ? "[\n" + gap
						+ partial.join(",\n" + gap) + "\n" + mind + "]" : "["
						+ partial.join(",") + "]";
				gap = mind;
				return v
			}
			if (rep && typeof rep === "object") {
				length = rep.length;
				for (i = 0; i < length; i += 1) {
					k = rep[i];
					if (typeof k === "string") {
						v = str(k, value);
						if (v) {
							partial.push(quote(k) + (gap ? ": " : ":") + v)
						}
					}
				}
			} else {
				for (k in value) {
					if (Object.hasOwnProperty.call(value, k)) {
						v = str(k, value);
						if (v) {
							partial.push(quote(k) + (gap ? ": " : ":") + v)
						}
					}
				}
			}
			v = partial.length === 0 ? "{}" : gap ? "{\n" + gap
					+ partial.join(",\n" + gap) + "\n" + mind + "}" : "{"
					+ partial.join(",") + "}";
			gap = mind;
			return v
		}
	}
	if (typeof JSON.stringify !== "function") {
		JSON.stringify = function(value, replacer, space) {
			var i;
			gap = "";
			indent = "";
			if (typeof space === "number") {
				for (i = 0; i < space; i += 1) {
					indent += " "
				}
			} else {
				if (typeof space === "string") {
					indent = space
				}
			}
			rep = replacer;
			if (replacer
					&& typeof replacer !== "function"
					&& (typeof replacer !== "object" || typeof replacer.length !== "number")) {
				throw new Error("JSON.stringify")
			}
			return str("", {
				"" : value
			})
		}
	}
	if (typeof JSON.parse !== "function") {
		JSON.parse = function(text, reviver) {
			var j;
			function walk(holder, key) {
				var k, v, value = holder[key];
				if (value && typeof value === "object") {
					for (k in value) {
						if (Object.hasOwnProperty.call(value, k)) {
							v = walk(value, k);
							if (v !== undefined) {
								value[k] = v
							} else {
								delete value[k]
							}
						}
					}
				}
				return reviver.call(holder, key, value)
			}
			cx.lastIndex = 0;
			if (cx.test(text)) {
				text = text.replace(cx, function(a) {
					return "\\u"
							+ ("0000" + a.charCodeAt(0).toString(16)).slice(-4)
				})
			}
			if (/^[\],:{}\s]*$/
					.test(text
							.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, "@")
							.replace(
									/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,
									"]").replace(/(?:^|:|,)(?:\s*\[)+/g, ""))) {
				j = eval("(" + text + ")");
				return typeof reviver === "function" ? walk( {
					"" : j
				}, "") : j
			}
			throw new SyntaxError("JSON.parse")
		}
	}
})();
TimeMap.prototype.toJSON = function() {
	var a = {
		options : this.makeOptionData,
		datasets : this.datasets
	};
	a = this.addExportData(a);
	return a
};
TimeMap.prototype.makeOptionData = function() {
	var e = {}, f = TimeMap.util;
	for ( var d in this.opts) {
		e[d] = this.opts[d]
	}
	if (e.mapCenter) {
		e.mapCenter = f.makePoint(e.mapCenter)
	}
	if (e.mapType) {
		e.mapType = f.revHash(TimeMap.mapTypes, e.mapType)
	}
	if (e.mapTypes) {
		var c = [], a;
		for ( var h = 0; h < e.mapTypes.length; h++) {
			a = f.revHash(TimeMap.mapTypes, e.mapTypes[h]);
			if (a) {
				c.push(a)
			}
		}
		e.mapTypes = c
	}
	if (e.bandIntervals) {
		e.bandIntervals = f.revHash(TimeMap.intervals, e.bandIntervals)
	}
	var g = [], i, b;
	for (b in this.datasets) {
		if (this.datasets.hasOwnProperty(b)) {
			i = f.revHash(TimeMapDataset.themes, this.datasets[b].opts.theme);
			if (i) {
				g.push(i)
			}
		}
	}
	e.themes = i;
	return e
};
TimeMap.prototype.addExportData = function(a) {
	a.options = a.options || {};
	a.options.saveOpts = this.opts.saveOpts;
	return a
};
TimeMapDataset.prototype.toJSON = function() {
	var a = {
		title : this.getTitle(),
		theme : TimeMap.util.revHash(TimeMapDataset.themes, this.opts.theme),
		data : {
			type : "basic",
			value : this.getItems()
		}
	};
	a = this.addExportData(a);
	return a
};
TimeMapDataset.prototype.addExportData = function(a) {
	a.options = a.options || {};
	a.options.saveOpts = this.opts.saveOpts;
	return a
};
TimeMapItem.prototype.toJSON = function() {
	var d = {
		title : this.getTitle(),
		options : {
			description : this.opts.description
		}
	};
	if (this.event) {
		d.start = this.event.getStart();
		if (!this.event.isInstant()) {
			d.end = this.event.getEnd()
		}
	}
	if (this.placemark) {
		var a = TimeMap.util;
		var c = function(h, g, i) {
			h = h || a.getPlacemarkType(g);
			switch (h) {
			case "marker":
				i.point = a.makePoint(g.getLatLng());
				break;
			case "polyline":
			case "polygon":
				var f = [];
				for ( var e = 0; e < g.getVertexCount(); e++) {
					f.push(a.makePoint(g.getVertex(e)))
				}
				i[h] = f;
				break
			}
			return i
		};
		if (this.getType() == "array") {
			d.placemarks = [];
			for ( var b = 0; b < this.placemark.length; b++) {
				d.placemarks.push(c(false, this.placemark[b], {}))
			}
		} else {
			d = c(this.getType(), this.placemark, d)
		}
	}
	d = this.addExportData(d);
	return d
};
TimeMapItem.prototype.addExportData = function(a) {
	a.options = a.options || {};
	a.options.saveOpts = this.opts.saveOpts;
	return a
};
TimeMap.util.revHash = function(b, c) {
	for ( var a in b) {
		if (b[a] == c) {
			return a
		}
	}
	return null
};
TimeMap.prototype.clear = function() {
	this.each(function(a) {
		a.clear()
	});
	this.datasets = []
};
TimeMap.prototype.deleteDataset = function(a) {
	this.datasets[a].clear();
	delete this.datasets[a]
};
TimeMap.prototype.hideDataset = function(a) {
	if (a in this.datasets) {
		this.datasets[a].hide()
	}
};
TimeMap.prototype.hideDatasets = function() {
	this.each(function(a) {
		a.visible = false
	});
	this.filter("map");
	this.filter("timeline");
	this.timeline.layout()
};
TimeMap.prototype.showDataset = function(a) {
	if (a in this.datasets) {
		this.datasets[a].show()
	}
};
TimeMap.prototype.showDatasets = function() {
	this.each(function(a) {
		a.visible = true
	});
	this.filter("map");
	this.filter("timeline");
	this.timeline.layout()
};
TimeMap.prototype.changeMapType = function(a) {
	if (a == this.opts.mapType) {
		return
	}
	if (typeof (a) == "string") {
		a = TimeMap.mapTypes[a]
	}
	if (!a) {
		return
	}
	this.opts.mapType = a;
	this.map.setMapType(a)
};
TimeMap.prototype.refreshTimeline = function() {
	var b = this.timeline.getBand(0);
	var a = b.getCenterVisibleDate();
	if (TimeMap.util.TimelineVersion() == "1.2") {
		b.getEventPainter().getLayout()._laidout = false
	}
	this.timeline.layout();
	b.setCenterVisibleDate(a)
};
TimeMap.prototype.changeTimeIntervals = function(c) {
	if (c == this.opts.bandIntervals) {
		return
	}
	if (typeof (c) == "string") {
		c = TimeMap.intervals[c]
	}
	if (!c) {
		return
	}
	this.opts.bandIntervals = c;
	var d = function(g, f) {
		g.getEther()._interval = Timeline.DateTime.gregorianUnitLengths[f];
		g.getEtherPainter()._unit = f
	};
	var e = this.timeline.getBand(0);
	var b = e.getCenterVisibleDate();
	for ( var a = 0; a < this.timeline.getBandCount(); a++) {
		d(this.timeline.getBand(a), c[a])
	}
	e.getEventPainter().getLayout()._laidout = false;
	this.timeline.layout();
	e.setCenterVisibleDate(b)
};
TimeMap.prototype.scrollTimeline = function(b) {
	var d = this.timeline.getBand(0);
	var a = d.getCenterVisibleDate();
	var c = a.getFullYear() + parseFloat(b);
	a.setFullYear(c);
	d.setCenterVisibleDate(a)
};
TimeMapDataset.prototype.clear = function() {
	this.each(function(a) {
		a.clear()
	});
	this.items = [];
	this.timemap.timeline.layout()
};
TimeMapDataset.prototype.deleteItem = function(b) {
	for ( var a = 0; a < this.items.length; a++) {
		if (this.items[a] == b) {
			b.clear();
			this.items.splice(a, 1);
			break
		}
	}
	this.timemap.timeline.layout()
};
TimeMapDataset.prototype.show = function() {
	if (!this.visible) {
		this.visible = true;
		this.timemap.filter("map");
		this.timemap.filter("timeline");
		this.timemap.timeline.layout()
	}
};
TimeMapDataset.prototype.hide = function() {
	if (this.visible) {
		this.visible = false;
		this.timemap.filter("map");
		this.timemap.filter("timeline");
		this.timemap.timeline.layout()
	}
};
TimeMapDataset.prototype.changeTheme = function(a) {
	this.opts.theme = a;
	this.each(function(b) {
		b.changeTheme(a)
	});
	this.timemap.timeline.layout()
};
TimeMapItem.prototype.show = function() {
	this.showEvent();
	this.showPlacemark();
	this.visible = true
};
TimeMapItem.prototype.hide = function() {
	this.hideEvent();
	this.hidePlacemark();
	this.visible = false
};
TimeMapItem.prototype.clear = function() {
	if (this.event) {
		this.dataset.timemap.timeline.getBand(0).getEventSource()._events._events
				.remove(this.event)
	}
	if (this.placemark) {
		this.hidePlacemark();
		var b = function(d) {
			try {
				this.map.removeOverlay(d)
			} catch (c) {
			}
		};
		if (this.getType() == "array") {
			for ( var a = 0; a < this.placemark.length; a++) {
				b(this.placemark[a])
			}
		} else {
			b(this.placemark)
		}
	}
	this.event = this.placemark = null
};
TimeMapItem.prototype.createEvent = function(b, d) {
	var a = (d === undefined);
	var f = this.getTitle();
	var c = new Timeline.DefaultEventSource.Event(b, d, null, null, a, f, null,
			null, null, this.opts.theme.eventIcon, this.opts.theme.eventColor,
			null);
	c.item = this;
	this.event = c;
	this.dataset.eventSource.add(c)
};
TimeMapItem.prototype.changeTheme = function(c) {
	this.opts.theme = c;
	if (this.placemark) {
		var b = function(d, e, f) {
			e = e || TimeMap.util.getPlacemarkType(d);
			switch (e) {
			case "marker":
				d.setImage(f.icon.image);
				break;
			case "polygon":
				d.setFillStyle( {
					color : c.fillColor,
					opacity : c.fillOpacity
				});
			case "polyline":
				d.setStrokeStyle( {
					color : c.lineColor,
					weight : c.lineWeight,
					opacity : c.lineOpacity
				});
				break
			}
		};
		if (this.getType() == "array") {
			for ( var a = 0; a < this.placemark.length; a++) {
				b(this.placemark[a], false, c)
			}
		} else {
			b(this.placemark, this.getType(), c)
		}
	}
	if (this.event) {
		this.event._color = c.eventColor;
		this.event._icon = c.eventIcon
	}
};
TimeMapItem.prototype.getNext = function(a) {
	if (!this.event) {
		return
	}
	var d = this.dataset.timemap.timeline.getBand(0).getEventSource();
	var b = d.getEventIterator(this.event.getStart(), new Date(d
			.getLatestDate().getTime() + 1));
	var c = null;
	while (c === null) {
		if (b.hasNext()) {
			c = b.next().item;
			if (a && c.dataset != this.dataset) {
				c = null
			}
		} else {
			break
		}
	}
	return c
};
TimeMap.loaders.flickr = function(b) {
	var a = new TimeMap.loaders.jsonp(b);
	a.preload = function(c) {
		return c.items
	};
	a.transform = function(d) {
		var c = {
			title : d.title,
			start : d.date_taken,
			point : {
				lat : d.latitude,
				lon : d.longitude
			},
			options : {
				description : d.description.replace(/&gt;/g, ">").replace(
						/&lt;/g, "<").replace(/&quot;/g, '"')
			}
		};
		if (b.transformFunction) {
			c = b.transformFunction(c)
		}
		return c
	};
	return a
};
TimeMap.loaders.georss = function(b) {
	var a = new TimeMap.loaders.remote(b);
	a.parse = TimeMap.loaders.georss.parse;
	return a
};
TimeMap.loaders.georss.parse = function(m) {
	var h = [], v, k, f, q;
	k = GXml.parse(m);
	var a = TimeMap.util;
	var c = a.getTagValue, r = a.getNodeList, o = a.makePoint, b = a.makePoly, g = a.formatDate, e = a.nsMap;
	e.georss = "http://www.georss.org/georss";
	e.gml = "http://www.opengis.net/gml";
	e.geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	e.kml = "http://www.opengis.net/kml/2.2";
	var t = (k.firstChild.tagName == "rss") ? "rss" : "atom";
	var p = (t == "rss" ? "item" : "entry");
	f = r(k, p);
	for ( var n = 0; n < f.length; n++) {
		q = f[n];
		v = {
			options : {}
		};
		v.title = c(q, "title");
		p = (t == "rss" ? "description" : "summary");
		v.options.description = c(q, p);
		var u = r(q, "TimeStamp", "kml");
		if (u.length > 0) {
			v.start = c(u[0], "when", "kml")
		}
		if (!v.start) {
			u = r(q, "TimeSpan", "kml");
			if (u.length > 0) {
				v.start = c(u[0], "begin", "kml");
				v.end = c(u[0], "end", "kml") || g(new Date())
			}
		}
		if (!v.start) {
			if (t == "rss") {
				var s = new Date(Date.parse(c(q, "pubDate")));
				v.start = g(s)
			} else {
				v.start = c(q, "updated")
			}
		}
		PLACEMARK: {
			var l, j;
			l = c(q, "point", "georss");
			if (l) {
				v.point = o(l);
				break PLACEMARK
			}
			u = r(q, "Point", "gml");
			if (u.length > 0) {
				l = c(u[0], "pos", "gml");
				if (!l) {
					l = c(u[0], "coordinates", "gml")
				}
				if (l) {
					v.point = o(l);
					break PLACEMARK
				}
			}
			if (c(q, "lat", "geo")) {
				l = [ c(q, "lat", "geo"), c(q, "long", "geo") ];
				v.point = o(l);
				break PLACEMARK
			}
			l = c(q, "line", "georss");
			if (l) {
				v.polyline = b(l);
				break PLACEMARK
			}
			l = c(q, "polygon", "georss");
			if (l) {
				v.polygon = b(l);
				break PLACEMARK
			}
			u = r(q, "LineString", "gml");
			if (u.length > 0) {
				j = "polyline"
			} else {
				u = r(q, "Polygon", "gml");
				if (u.length > 0) {
					j = "polygon"
				}
			}
			if (u.length > 0) {
				l = c(u[0], "posList", "gml");
				if (!l) {
					l = c(u[0], "coordinates", "gml")
				}
				if (l) {
					v[j] = b(l);
					break PLACEMARK
				}
			}
		}
		h.push(v)
	}
	k = null;
	f = null;
	q = null;
	u = null;
	return h
};
TimeMap.loaders.gss = function(b) {
	var a = new TimeMap.loaders.jsonp(b);
	if (!a.url) {
		a.url = "http://spreadsheets.google.com/feeds/list/" + b.key
				+ "/1/public/values?alt=json-in-script&callback="
	}
	a.map = b.map;
	a.preload = function(c) {
		return c.feed["entry"]
	};
	a.transform = function(f) {
		var d = a.map || TimeMap.loaders.gss.map;
		var c = function(g) {
			if (g in d && d[g]) {
				return f["gsx$" + d[g]]["$t"]
			} else {
				return false
			}
		};
		var e = {
			title : c("title"),
			start : c("start"),
			point : {
				lat : c("lat"),
				lon : c("lon")
			},
			options : {
				description : c("description")
			}
		};
		if (b.transformFunction) {
			e = b.transformFunction(e)
		}
		return e
	};
	return a
};
TimeMap.loaders.gss.map = {
	title : "title",
	description : "description",
	start : "start",
	end : "end",
	lat : "lat",
	lon : "lon"
};
TimeMap.loaders.jsonp = function(a) {
	TimeMap.loaders.mixin(this, a);
	this.url = a.url
};
TimeMap.loaders.jsonp.prototype.load = function(b, c) {
	var a = this;
	TimeMap.loaders.jsonp.read(this.url, function(d) {
		items = a.preload(d);
		b.loadItems(items, a.transform);
		c()
	})
};
TimeMap.loaders.jsonp.counter = 0;
TimeMap.loaders.jsonp.read = function(b, c) {
	var d = "_" + TimeMap.loaders.jsonp.counter++;
	TimeMap.loaders.jsonp[d] = function(e) {
		c(e);
		delete TimeMap.loaders.jsonp[d]
	};
	var a = document.createElement("script");
	a.src = b + "TimeMap.loaders.jsonp." + d;
	document.body.appendChild(a)
};
TimeMap.loaders.json_string = function(b) {
	var a = new TimeMap.loaders.remote(b);
	a.parse = JSON.parse;
	return a
};
TimeMap.loaders.json = TimeMap.loaders.jsonp;
TimeMap.loaders.kml = function(b) {
	var a = new TimeMap.loaders.remote(b);
	a.parse = TimeMap.loaders.kml.parse;
	return a
};
TimeMap.loaders.kml.parse = function(t) {
	var g = [], s, n, e, o, l, h;
	n = GXml.parse(t);
	var a = TimeMap.util;
	var c = a.getTagValue, q = a.getNodeList, m = a.makePoint, b = a.makePoly, f = a.formatDate;
	var d = function(w, v) {
		var i = false;
		var u = q(w, "TimeStamp");
		if (u.length > 0) {
			v.start = c(u[0], "when");
			i = true
		} else {
			u = q(w, "TimeSpan");
			if (u.length > 0) {
				v.start = c(u[0], "begin");
				v.end = c(u[0], "end") || f(new Date());
				i = true
			}
		}
		if (!i) {
			var j = w.parentNode;
			if (j.nodeName == "Folder" || j.nodeName == "Document") {
				d(j, v)
			}
			j = null
		}
	};
	e = q(n, "Placemark");
	for (l = 0; l < e.length; l++) {
		o = e[l];
		s = {
			options : {}
		};
		s.title = c(o, "name");
		s.options.description = c(o, "description");
		d(o, s);
		var r, k, p;
		s.placemarks = [];
		r = q(o, "Point");
		for (h = 0; h < r.length; h++) {
			p = {
				point : {}
			};
			k = c(r[h], "coordinates");
			p.point = m(k, 1);
			s.placemarks.push(p)
		}
		r = q(o, "LineString");
		for (h = 0; h < r.length; h++) {
			p = {
				polyline : []
			};
			k = c(r[h], "coordinates");
			p.polyline = b(k, 1);
			s.placemarks.push(p)
		}
		r = q(o, "Polygon");
		for (h = 0; h < r.length; h++) {
			p = {
				polygon : []
			};
			k = c(r[h], "coordinates");
			p.polygon = b(k, 1);
			s.placemarks.push(p)
		}
		g.push(s)
	}
	e = q(n, "GroundOverlay");
	for (l = 0; l < e.length; l++) {
		o = e[l];
		s = {
			options : {},
			overlay : {}
		};
		s.title = c(o, "name");
		s.options.description = c(o, "description");
		d(o, s);
		r = q(o, "Icon");
		s.overlay.image = c(r[0], "href");
		r = q(o, "LatLonBox");
		s.overlay.north = c(r[0], "north");
		s.overlay.south = c(r[0], "south");
		s.overlay.east = c(r[0], "east");
		s.overlay.west = c(r[0], "west");
		g.push(s)
	}
	n = null;
	e = null;
	o = null;
	r = null;
	return g
};
TimeMap.loaders.metaweb = function(b) {
	var a = new TimeMap.loaders.jsonp(b);
	a.HOST = b.host || "http://www.freebase.com";
	a.QUERY_SERVICE = b.service || "/api/service/mqlread";
	a.preload = function(f) {
		var g = f.qname;
		if (g.code.indexOf("/api/status/ok") != 0) {
			return []
		}
		var e = g.result;
		return e
	};
	var d = b.query || {};
	var c = encodeURIComponent(JSON.stringify( {
		qname : {
			query : d
		}
	}));
	a.url = a.HOST + a.QUERY_SERVICE + "?queries=" + c + "&callback=";
	return a
};