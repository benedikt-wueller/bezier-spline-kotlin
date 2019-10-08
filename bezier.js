/* bezier-spline.js
 *
 * computes cubic bezier coefficients to generate a smooth
 * line through specified points. couples with SVG graphics
 * for interactive processing.
 *
 * For more info see:
 * http://www.particleincell.com/2012/bezier-splines/
 *
 * Lubos Brieda, Particle In Cell Consulting LLC, 2012
 * you may freely use this algorithm in your codes however where feasible
 * please include a link/reference to the source article
 *
 * correction by Jaco Stuifbergen:
 * in computeControlPoints:
 *	r[n-1] = 3*K[n-1]; // otherwise, the curvature on the last knot is wrong
 *
 * modification:
 * the distance of control points is proportional to the distance between knots
I.e. if D(x1,x2) is the distance between x1 and x2,
 and P1[i] , P2[i] are the control points between knots K[i] and K[i+1]
then
 D(P2[i-1],K[i]) / D(K[i-1],K[i]) = D(K[i],P1[i]) / D(K[i],K[i+1])
 */

var svg=document.documentElement /*svg object*/
var S=new Array() /*splines*/
var V=new Array() /*vertices*/
// var movingKnot 	/*current object*/
var x0,y0	/*svg offset*/
var colours = [ "blue", "red", "green", "brown", "yellow", "magenta"]
var pathIdPrefix = "pathNr_"
var knotIdPrefix = "knotNr_"
var pathWidth = 4
var minWeight = 1 // the calculation of a curve becomes impossible if a distance is 0
var circular = true // the spline will be a loop
var nKnots = -1 // no Knots are defined
var nPaths = circular?nKnots:nKnots-1

/*saves elements as global variables*/
function init()
{
	/*create control points*/
	var coordString = "{\"x\":[60,220,389,700,600,474,330],\"y\":[60,300,242,240,100,287,390]}"
	//var coordString = "{\"x\":[60,220,220,60],\"y\":[60,60,300,300]}"
	//var coordString = "{\"x\":[60,220,220],\"y\":[60,60,300]}"

	/* create knots and paths */
	stringToKnots(coordString) // create knots from {x:[], y:[]} to [{x, y}, {x, y}]
	// createAllPaths()

	/*calculate splines*/
	//updateSplines();

}

/* return x and y values of knots */
function knotCoord()
{
	var i, x, y
	x = new Array(nKnots)
	y = new Array(nKnots)
	for (i=0;i<nKnots;i++)
	{
		/*use parseInt to convert string to int*/
		x[i]=parseInt(V[i].getAttributeNS(null,"cx"))
		y[i]=parseInt(V[i].getAttributeNS(null,"cy"))
	}
	return {x:x,y:y};
}

/*computes spline control points*/
function updateSplines()
{
	var coord, x, y /* (x,y) coordinates of the knots*/
	var weights // equal to the distances between knots. If knots are nearer, the 3rd derivative can be higher
	var px, py // coordinates of the intermediate control points
	// for debugging
	var dx, dy

	// tijdelijk, voor foutopsporing
	var d1,d2

	/*grab (x,y) coordinates of the knots */
	coord = knotCoord();
	x = coord.x
	y = coord.y

	weights = new Array(nPaths)

	weighting = function(x1,x2,y1,y2)
	{
		/* calculate Euclidean distance */
		var d = Math.sqrt(Math.pow((x1-x2),2) +Math.pow((y1-y2),2))
		// if the weight is too small, the calculation becomes instable
		return d<minWeight?minWeight:d
	}

	for (i=0;i<nKnots-1;i++)
	{
		weights[i] = weighting(x[i+1],x[i],y[i+1],y[i])
	}
	if (circular) // one more path, so one more weight
	{
		// i = nPaths-1 = nKnots-1
		weights[i] = weighting(x[0],x[i],y[0],y[i])
	}
	/* calculation of the curve */
	/* Several ways have been programmed to calculate the curves:
		- circular or not
		- weighted or not
		- using big or small matrices

	 * circular : the curve is a loop
	 *
	 * weights:
	 * W[i] = D(K[i],K[i+1])
	 * (where D(M,N) denotes the distance between M and N)
	 * the "weighted" algorithms impose
		D(P2[i-1],K[i]) / W[i-1] = D(K[i],P1[i]) / W[i])
	 * (so if the distance between nodes is short, the control points will be near)
	 *
	 * Big or small matrices: "big" matrices include the equation
		D(P2[i-1],K[i]) / W[i-1] = D(K[i],P1[i]) / W[i])
		or
		D(P2[i-1],K[i]) = D(K[i],P1[i])
		 (for the "unweighted" version)
	   in the "small" matrices, the variables P2 have already been eliminated.
	*/

	if (circular)
	{
	//	px = computeControlPointsCircular(x); py = computeControlPointsCircular(y);
	//	px = computeControlPointsCircularBig(x); py = computeControlPointsCircularBig(y);
		px = computeControlPointsCircularW(x,weights);  py = computeControlPointsCircularW(y,weights);

		/* controleer verschillen */
		// dx = computeControlPointsCircularWBig(x,weights); dy = computeControlPointsCircularWBig(y,weights);
	}
	else
	{
	//	px = computeControlPoints(x); py = computeControlPoints(y);
	// dx = computeControlPointsBigThomas(x); dy = computeControlPointsBigThomas(y);


	/* version that uses weighting
	- control points are nearer if the knots are near each other
	- 2nd derivatives remain continuous
	- 3rd derivatives may become bigger if knots are near each other */

	// px = computeControlPointsBigWThomas(x,weights);
	// py = computeControlPointsBigWThomas(y,weights);

	/* also with weighting, but more elegant*/
	px = computeControlPointsW(x,weights);
	py = computeControlPointsW(y,weights);
	}

	/*updates path settings, the browser will draw the new spline*/
	for (i=0;i<nKnots-1;i++)
	{	S[i].setAttributeNS(null,"d",
			pathDescription(x[i],y[i],px.p1[i],py.p1[i],px.p2[i],py.p2[i],x[i+1],y[i+1]));
	}
	if (circular) // for circular loops
	{	S[i].setAttributeNS(null,"d",
			pathDescription(x[i],y[i],px.p1[i],py.p1[i],px.p2[i],py.p2[i],x[0],y[0]));
	}
}

/*creates formated path string for SVG cubic path element*/
function pathDescription(x1,y1,px1,py1,px2,py2,x2,y2)
{
	return "M "+x1+" "+y1+" C "+px1+" "+py1+" "+px2+" "+py2+" "+x2+" "+y2;
}

/*computes control points given knots K, this is the brain of the operation*/
function computeControlPointsCircular(K)
{
/* in deze versie geldt s_i'(1)=s[i+1]'(0), dus continuïteit van s(t)
Als knopen dicht bij elkaar liggen, zal de curve er doorheen slalommen
*/
	//document.getElementById("testje").innerHTML = "computeControlPointsCircular"
	var p1, p2, n
	var a,b,c,r
	p2=new Array();
	n = K.length;

	/*rhs vector*/
	a=new Array();
	b=new Array();
	c=new Array();
	r=new Array();

	/*left most segment*/
	// a[0] is the upper right corner of the matrix

	/*internal segments*/
	for (i = 0; i < n-1 ; i++)
	{
		a[i]=1;
		b[i]=4;
		c[i]=1;
		r[i] = 4 * K[i] + 2 * K[i+1];
	}

	/*last segment*/
	a[n-1]=1;
	b[n-1]=4;
	// c[n-1] is the lower left corner of the matrix
	c[n-1]=1
	// a_n1=1;
	r[n-1] = 4 * K[n-1] + 2 * K[0];

	/*solves Ax=b with the Thomas algorithm (from Wikipedia)*/
	// p1=gauss(convertThomasToGauss(r,a,b,c))
	// p1=ThomasCircularGauss(r,a,b,c,a_1n,a_n1)
	// p1=ThomasCircular_temp(r,a,b,c,a_1n,a_n1)
	p1=ThomasCircular_new(r,a,b,c)

	/*we have p1, now compute p2*/
	for (i=0;i<n-1;i++)
		p2[i]=2*K[i+1]-p1[i+1];
	// now, i = n-1
	p2[i]=2*K[0]-p1[0];

	return {p1:p1, p2:p2};
}
/*computes control points given knots K, this is the brain of the operation*/
function computeControlPointsCircularBig(K)
/* 	uses a bigger matrix, for comparison
*/
{
	//document.getElementById("testje").innerHTML = "computeControlPointsCircularBig"
	var p, p1, p2
	p = new Array();

	p1=new Array();
	p2=new Array();
	n = K.length;

	/*rhs vector*/
	a=new Array();
	b=new Array();
	c=new Array();
	d=new Array();
	r=new Array();

	/*internal segments*/
	for (i = 0; i < n ; i++)
	{
		a[2*i]=1; // a[0] is the right upper element of the matrix
		b[2*i]=1; // b are the diagonal elements
		c[2*i]=0; // 1 column right of the diagonal
		d[2*i]=0; // 1 column right of c, execpt d[2n-2] and d[2n-1]
		r[2*i] = 2*K[i];

		a[2*i+1]=1;
		b[2*i+1]=-2;
		c[2*i+1]=2; // c[2n-1] is the element in the left lower corner
		d[2*i+1]=-1
		r[2*i+1] = 0;

	}

	/*solves Ax=b with the Thomas algorithm (from Wikipedia)*/
	// p = Thomas4(r,a,b,c,d)
	p = gauss(convertThomasToGauss4(r,a,b,c,d))

	/*re-arrange the array*/
	for (i=0;i<n;i++)
	{
		p1[i]=p[2*i];
		p2[i]=p[2*i+1];
	}

	return {p1:p1, p2:p2};
}



/*computes control points given knots K, this is the brain of the operation*/
function computeControlPoints(K)
{
/* in deze versie geldt s_i'(1)=s[i+1]'(0), dus continuïteit van s(t)
Als knopen dicht bij elkaar liggen, zal de curve er doorheen slalommen
*/
	//document.getElementById("testje").innerHTML = "computeControlPoints"
	var p1, p2, n
	var a,b,c,r
	p2=new Array();
	n = K.length;

	/*rhs vector*/
	a=new Array();
	b=new Array();
	c=new Array();
	r=new Array();

	/*left most segment*/
	a[0]=0; // outside the matrix
	b[0]=2;
	c[0]=1;
	r[0] = K[0]+2*K[1];

	/*internal segments*/
	for (i = 1; i < n - 1; i++)
	{
		a[i]=1;
		b[i]=4;
		c[i]=1;
		r[i] = 4 * K[i] + 2 * K[i+1];
	}

	/*right segment*/
	a[n-1]=1;
	b[n-1]=2;
	c[n-1]=0; // outside the matrix
	r[n-1] = 3*K[n-1];

	/*solves Ax=b with the Thomas algorithm (from Wikipedia)*/
	p1=Thomas(r,a,b,c)

	/*we have p1, now compute p2*/
	for (i=0;i<n-1;i++)
		p2[i]=2*K[i+1]-p1[i+1];

	/* the last element of p1 is only used to calculate p2 */
	p1.splice(n-1,1) // remove the last element
	return {p1:p1, p2:p2};
}

function computeControlPointsCircularW(values,weights)
/*computes control points given knots K, this is the brain of the operation*/
/* this version makes the distance of the control points proportional to the distance between the end points.
I.e. if D(x1,x2) is the distance between x1 and x2,
 and P1[i] , P2[i] are the control points between knots K[i] and K[i+1]
then
 D(P2[i-1],K[i]) / D(K[i-1],K[i]) = D(K[i],P1[i]) / D(K[i],K[i+1])

The calculation of the second derivative has been adapted in 2 ways:
If W[i]=D(K[i-1],K[i])/D(K[i+1],K[i])
1) 	P2[i-1] = P1[i-1]*W +K[i]*(W[i]+1)
2)	S''[i](0)*W[i]*W[i]=S''[i-1](1)
*/

// required: W has the same length als K
// calculates the control points (for 1 dimension) of a Béziers curve through the points K, using weights W.
// K should contain all the x-coordinates (or all the y-coordinates)
// K has indices 0..n-1

{
	//document.getElementById("testje").innerHTML = "computeControlPointsCircularW"
	var W = weights.slice() // make a copy of the array
	var K = values.slice()

	var p1, p2, n
	var frac_i

	p2=new Array();
	n = K.length;

	/*rhs vector*/
	a=new Array();
	b=new Array();
	c=new Array();
	r=new Array();


	/*internal segments*/
	// required: W[n] exists, the same length als K
	W[-1]=W[n-1]
	W[n]=W[0]
	K[n]=K[0]
	for (i = 0; i < n ; i++)
	{
		frac_i=W[i]/W[i+1]
		a[i]=1*W[i]*W[i];
		b[i]=2*W[i-1]*(W[i-1]+W[i]);
		c[i]=W[i-1]*W[i-1]*frac_i // W[i]/W[i+1];
		r[i] = Math.pow(W[i-1]+W[i],2) * K[i] + Math.pow(W[i-1],2)*(1+frac_i) * K[i+1];

	}

	/*solves Ax=b with the Thomas algorithm (from Wikipedia)*/
	//p1=parent.Thomas(r,a,b,c)
	/* The previous line provokes the error: (on Chrome, under windows)
	Uncaught SecurityError: Blocked a frame with origin "null" from accessing a frame with origin "null". Protocols, domains, and ports must match.
	In that case, a local function definition would work.
	But I fear that passing data between SVG frames is not possible in such a situation !!!
	*/
	// p1=gauss(convertThomasToGauss(r,a,b,c))
	p1=ThomasCircular_new(r,a,b,c)

	/*we have p1, now compute p2*/
	// required: p1[n] exists
	p1[n]=p1[0]
	for (i=0;i<n;i++)
	{	//p2[i]=2*K[i+1]-p1[i+1];
		p2[i]=K[i+1]* (1+W[i]/W[i+1])-p1[i+1]*(W[i]/W[i+1]);
	}
		/* alert(" computeControlPointsW i = "+i
		+"\nK = "+K.concat()
		+"\nW = "+W.concat()
		+"\na = "+a.concat()
		+"\nb = "+b.concat()
		+"\nc = "+c.concat()
		+"\nr = "+r.concat()
		+"\np1 = "+p1.concat()
		+"\np2 = "+p2.concat()
		) */
	return {p1:p1, p2:p2};
}

function computeControlPointsW(K,weigths)
/*computes control points given knots K, this is the brain of the operation*/
/* this version makes the distance of the control points proportional to the distance between the end points.
I.e. if D(x1,x2) is the distance between x1 and x2,
 and P1[i] , P2[i] are the control points between knots K[i] and K[i+1]
then
 D(P2[i-1],K[i]) / D(K[i-1],K[i]) = D(K[i],P1[i]) / D(K[i],K[i+1])

The calculation of the second derivative has been adapted in 2 ways:
If W[i]=D(K[i-1],K[i])/D(K[i+1],K[i])
1) 	P2[i-1] = P1[i-1]*W +K[i]*(W[i]+1)
2)	S''[i](0)*W[i]*W[i]=S''[i-1](1)
*/

// required: W has the same length als K
// calculates the control points (for 1 dimension) of a Béziers curve through the points K, using weights W.
// K should contain all the x-coordinates (or all the y-coordinates)
// K has indices 0..n-1

{
	//document.getElementById("testje").innerHTML = "computeControlPointsW"
	W = weights.slice() // make a copy, because W will be made longer
	var p1, p2, n
	var frac_i

	p2=new Array();
	n = K.length;
	W[n-1]=W[n-2]
	/*rhs vector*/
	a=new Array();
	b=new Array();
	c=new Array();
	r=new Array();


	/*left most segment*/
	frac_i=W[0]/W[1]
	a[0]=0; // outside the matrix
	b[0]=2;
	c[0]= frac_i // = W[0]/W[1]
	r[0] = K[0]+(1+frac_i)*K[1];

	/*internal segments*/
	// required: W has the same length als K
	for (i = 1; i < n - 1; i++)
	{
		frac_i=W[i]/W[i+1]
		a[i]=1*W[i]*W[i];
		b[i]=2*W[i-1]*(W[i-1]+W[i]);
		c[i]=W[i-1]*W[i-1]*frac_i // W[i]/W[i+1];
		r[i] = Math.pow(W[i-1]+W[i],2) * K[i] + Math.pow(W[i-1],2)*(1+frac_i) * K[i+1];

	}
	/*right segment*/
	a[n-1]=1;
	// fout: b[n-1]=2*W[n-1]/W[n-2];// gecorrigeerd op 2017-04-16 voor als W[n-1]<>W[n-2]
	// n-1=nPaths, W[n-1] is not defined
	// ( we could define W[n-1]=W[n-2] to make the curvature 0 at the end)
	b[n-1]=2;// gecorrigeerd op 2017-11-26:
	c[n-1]=0; // outside the matrix
	r[n-1] = 3*K[n-1] // before: (1+2*W[n-1]/W[n-2])*K[n-1]; // W[n-1] must be defined

	/*solves Ax=b with the Thomas algorithm (from Wikipedia)*/
	//p1=parent.Thomas(r,a,b,c)
	/* The previous line provokes the error: (on Chrome, under windows)
	Uncaught SecurityError: Blocked a frame with origin "null" from accessing a frame with origin "null". Protocols, domains, and ports must match.
	In that case, a local function definition would work.
	But I fear that passing data between SVG frames is not possible in such a situation !!!
	*/
	p1=Thomas(r,a,b,c)

	/*we have p1, now compute p2*/
	// required: W has the same length als K
	for (i=0;i<n-1;i++)
	{	//p2[i]=2*K[i+1]-p1[i+1];
		p2[i]=K[i+1]* (1+W[i]/W[i+1])-p1[i+1]*(W[i]/W[i+1]);
	}
	// for i=n-2, this yields:
	//	p2[n-2]=2*K[n-1]-p1[n-1];

	/* the last element of p1 is only used to calculate p2 */
	p1.splice(n-1,1) // remove the last element
	return {p1:p1, p2:p2};
}

/*computes control points given knots K, this is the brain of the operation*/
function computeControlPointsBigThomas(K)
{
	//document.getElementById("testje").innerHTML = "computeControlPointsBigThomas"
	var p, p1, p2
	p = new Array();

	p1=new Array();
	p2=new Array();
	n = K.length-1;

	/*rhs vector*/
	a=new Array();
	b=new Array();
	c=new Array();
	d=new Array();
	r=new Array();

	/*left most segment*/
	a[0]=0; // outside the matrix
	b[0]=2;
	c[0]=-1;
	d[0]=0
	r[0] = K[0]+0;// add curvature at K0

	/*internal segments*/
	for (i = 1; i < n ; i++)
	{
		a[2*i-1]=1;
		b[2*i-1]=-2;
		c[2*i-1]=2;
		d[2*i-1]=-1
		r[2*i-1] = 0;

		a[2*i]=1;
		b[2*i]=1;
		c[2*i]=0;
		d[2*i]=0; // note: d[2n-2] is already outside the matrix
		r[2*i] = 2*K[i];

	}

	/*right segment*/
	a[2*n-1]=-1;
	b[2*n-1]=2;
	r[2*n-1]=K[n];// curvature at last point

	// the following array elements are not in the original matrix, so they should not be used:
	c[2*n-1]=0; // outside the matrix
	d[2*n-2]=0; // outside the matrix
	d[2*n-1]=0; // outside the matrix

	/*solves Ax=b with the Thomas algorithm (from Wikipedia)*/
	p = Thomas4(r,a,b,c,d)

	/*re-arrange the array*/
	for (i=0;i<n;i++)
	{
		p1[i]=p[2*i];
		p2[i]=p[2*i+1];
	}

	return {p1:p1, p2:p2};
}

/*computes control points given knots K, this is the brain of the operation*/
/* this version makes the distance of the control points proportional to the distance between the end points.
I.e. if D(x1,x2) is the distance between x1 and x2,
 and P1[i] , P2[i] are the control points between knots K[i] and K[i+1]
then
 D(P2[i-1],K[i]) / D(K[i-1],K[i]) = D(K[i],P1[i]) / D(K[i],K[i+1])

The calculation of the second derivative has been adapted in 2 ways:
If W[i]=D(K[i-1],K[i])/D(K[i+1],K[i])
1) 	P2[i-1] = P1[i-1]*W +K[i]*(W[i]+1)
2)	S''[i](0)*W[i]*W[i]=S''[i-1](1)
*/

function computeControlPointsBigWThomas(K,W)
{
	//document.getElementById("testje").innerHTML = "computeControlPointsBigWThomas"
	var p, p1, p2
	p = new Array();

	p1=new Array();
	p2=new Array();
	n = K.length-1;

	/*rhs vector*/
	a=new Array();
	b=new Array();
	c=new Array();
	d=new Array();
	r=new Array();

	/*left most segment*/
	a[0]=0; // outside the matrix
	b[0]=2;
	c[0]=-1;
	d[0]=0
	r[0] = K[0]+0;// add curvature at K0

	/*internal segments*/
	for (i = 1; i < n ; i++)
	{
		a[2*i-1]=1*W[i]*W[i];
		b[2*i-1]=-2*W[i]*W[i];
		c[2*i-1]=2*W[i-1]*W[i-1];
		d[2*i-1]=-1*W[i-1]*W[i-1]
		r[2*i-1] = K[i]*((-W[i]*W[i]+W[i-1]*W[i-1]))//

		a[2*i]=W[i];
		b[2*i]=W[i-1];
		c[2*i]=0;
		d[2*i]=0; // note: d[2n-2] is already outside the matrix
		r[2*i] = (W[i-1]+W[i])*K[i];

	}

	/*right segment*/
	a[2*n-1]=-1;
	b[2*n-1]=2;
	r[2*n-1]=K[n];// curvature at last point

	// the following array elements are not in the original matrix, so they should not be used:
	c[2*n-1]=0; // outside the matrix
	d[2*n-2]=0; // outside the matrix
	d[2*n-1]=0; // outside the matrix

	/*solves Ax=b with the Thomas algorithm (from Wikipedia)*/
	p = Thomas4(r,a,b,c,d)

	/*re-arrange the array*/
	for (i=0;i<n;i++)
	{
		p1[i]=p[2*i];
		p2[i]=p[2*i+1];
	}

	return {p1:p1, p2:p2};
}

/*computes control points given knots K, this is the brain of the operation*/
/* this version makes the distance of the control points proportional to the distance between the end points.
I.e. if D(x1,x2) is the distance between x1 and x2,
 and P1[i] , P2[i] are the control points between knots K[i] and K[i+1]
then
 D(P2[i-1],K[i]) / D(K[i-1],K[i]) = D(K[i],P1[i]) / D(K[i],K[i+1])

The calculation of the second derivative has been adapted in 2 ways:
If W[i]=D(K[i-1],K[i])/D(K[i+1],K[i])
1) 	P2[i-1] = P1[i-1]*W +K[i]*(W[i]+1)
2)	S''[i](0)*W[i]*W[i]=S''[i-1](1)
*/

function computeControlPointsCircularWBig(values,weights)
{
	//document.getElementById("testje").innerHTML = "computeControlPointsCircularWBig"
	var W = weights.slice() // make a copy of the array
	var K = values.slice()

	var p, p1, p2
	p = new Array();

	p1=new Array();
	p2=new Array();
	n = K.length;
	/*rhs vector*/
	a=new Array();
	b=new Array();
	c=new Array();
	d=new Array();
	r=new Array();

	/*left most segment
	a[0]=0; // outside the matrix
	b[0]=2;
	c[0]=-1;
	d[0]=0
	r[0] = K[0]+0;// add curvature at K0
	*/
	W[-1]=W[n-1]
	W[n]=W[0]
	K[n]=K[0]
	/*internal segments*/
	for (i = 0; i < n ; i++)
	{
		a[2*i]=W[i];
		b[2*i]=W[i-1];
		c[2*i]=0;
		d[2*i]=0; // note: d[2n-2] is already outside the matrix
		r[2*i] = (W[i-1]+W[i])*K[i];

		j=i+1
		a[2*i+1]=1*W[i+1]*W[i+1];
		b[2*i+1]=-2*W[i+1]*W[i+1];
		c[2*i+1]=2*W[i]*W[i];
		d[2*i+1]=-1*W[i]*W[i]
		r[2*i+1] = K[i+1]*((-W[i+1]*W[i+1]+W[i]*W[i]))//
	}
	/*convert to a matrix and solves Ax=b (from Wikipedia)*/
	p = gauss(convertThomasToGauss4(r,a,b,c,d))

	/*re-arrange the array*/
	for (i=0;i<n;i++)
	{
		p1[i]=p[2*i];
		p2[i]=p[2*i+1];
	}

	return {p1:p1, p2:p2};
}

/*solves Ax=b with the Thomas algorithm (from Wikipedia)*/
/* essentially, a Gaussian elimination for a tri-diagonal matrix
*/
function Thomas(r,a,b,c)
{
	var x,i,n
	n = r.length
	for (i = 1; i < n; i++)
	{
		m = a[i]/b[i-1];
		b[i] = b[i] - m * c[i - 1];
		r[i] = r[i] - m*r[i-1];
	}

	x= new Array(n)
	x[n-1] = r[n-1]/b[n-1];
	for (i = n - 2; i >= 0; --i)
	{	x[i] = (r[i] - c[i] * x[i+1]) / b[i];
	}
	return x;
}

function Thomas4(r,a,b,c,d)
{
	var p,i,n,m
	n = r.length
	p = new Array(n)

	// the following array elements are not in the original matrix, so they should not have an effect
	a[0]=0; // outside the matrix
	c[n-1]=0; // outside the matrix
	d[n-2]=0; // outside the matrix
	d[n-1]=0; // outside the matrix

	/*solves Ax=b with the Thomas algorithm (from Wikipedia)*/
	/* adapted for a 4-diagonal matrix. only the a[i] are under the diagonal, so the Gaussian elimination is very similar */
	for (i = 1; i < n; i++)
	{
		m = a[i]/b[i-1];
		b[i] = b[i] - m * c[i - 1];
		c[i] = c[i] - m * d[i - 1];
		r[i] = r[i] - m * r[i-1];
	}

	p[n-1] = r[n-1]/b[n-1];
	p[n-2] = (r[n-2] - c[n-2] * p[n-1]) / b[n-2];
	for (i = n - 3; i >= 0; --i)
	{	p[i] = (r[i] - c[i] * p[i+1]-d[i]*p[i+2]) / b[i];
	}
/*
	p[n] = 0 // c[n-1] and d[n-2] are outside the matrix
	p[n+1]=0 // d[n-1] is outside the matrix
	for (i = n - 1; i >= 0; --i)
	{	p[i] = (r[i] - c[i] * p[i+1]-d[i]*p[i+2]) / b[i];
	}
*/
	return p
}
function ThomasCircular_temp(r_in,a_in,b_in,c_in,a1n, an1)
{
	a_in[0]=a1n
	c_in[c_in.length]=an1

	return ThomasCircular_new(r_in,a_in,b_in,c_in)
}
/*solves Ax=r by Guassian elimination (like the Thomas algorithm (from Wikipedia))
 	r: right-hand vector
	a: array of the sub-diagonal elements (indexes 1 till n-1)
	a[0] is the most up most right element (position [0,n-1])
	b: array of the diagonal elements(indexes 0 till n-1)
	c: array of the upper-diagonal elements(indexed 0 till n-2)
	c[n-1] is the lowest most left element (position [n-1,0])
	all other elements are supposed to be 0
  |   b0 c0 0  0  ...    .      .      .      a0    |
  |   a1 b1 c1 0  ...    .      .      .      0     |
  |   0  a2 b2 c2 ..     .      .      .      0     |
  |   .   . .  .  ...    .      .      .      .     |  x = r
  |   .   . .  .  ...    .      .      .      .     |
  |   0   0 0  0  ... a[n-3] b[n-3] c[n-3]    0     |
  |   0   0 0  0  ...    0   a[n-2] b[n-2]  c[n-2]  |
  |c[n-1] 0 0  0  ...    0      0   a[n-1]  b[n-1]  |
*/
function ThomasCircular_new(r_in,a_in,b_in,c_in)
{
	var x,i,n,m
	var r = r_in.slice() // creates a copy
	var a = a_in.slice() // creates a copy
	var b = b_in.slice() // creates a copy
	var c = c_in.slice() // creates a copy
	n = r.length

	// lastcolumn
	// for lc, indexes 0 till n-3 are used.
	// lc[n-2] is not used, use c[n-2]
	// lc[n-1] is not used, use b[n-1]
	var lc = new Array(n)
	lc[0] = a[0]

	// last row
	// lr contains a value from the last row
	// indexes 0 till n-3 are used.
	// lr[n-2] is not used, use a[n-1]
	// lr[n-1] is not used, use b[n-1]
	var lr = c[n-1]

	for (i = 0; i < n-3; i++)
	{
		m = a[i+1]/b[i];
		b[i+1] -= m * c[i];
		r[i+1] -= m * r[i];
		// last column // superflous when i=n-2
		lc[i+1] = -m * lc[i]

		// last row : lr=lr[i]
		m = lr/b[i]
		b[n-1] -= m * lc[i]
		lr = - m * c[i] // lr=lr[i+1], superflous when i=n-2
		// lr[i]=0 maar deze waarde wordt niet meer gebruikt
		r[n-1] -= m * r[i]
	}
	// note that i = n-3 now
	{
		m = a[i+1]/b[i];
		b[i+1] -= m * c[i];
		r[i+1] -= m * r[i];
		// last column
		// in stead of lc[i+1]=-m*lc[i]
		c[i+1] -= m * lc [i]
		// last row
		m = lr/b[i]
		b[n-1] -= m * lc[i]
		// in stead of lr[i+1]= - m * c[i] // superflous when i=n-2
		a[n-1] -= m * c[i]
		// lr[i]=0 maar deze waarde wordt niet meer gebruikt
		r[n-1]= r[n-1] - m * r[i]
	}
	i = n-2
	// in stead of: for (i = n-1; i < n; i++)
	{
		m = a[i+1]/b[i];
		// in stead of lr[i+1]= - m * c[i]
		b[i+1] -= m * c[i];
		// in stead of r[n-1]= r[n-1] - m * r[i] // reeds gedaan
		r[i+1] -= m * r[i];
	}

	x= new Array(n)

	x[n-1] = r[n-1]/b[n-1];
	// the value of lc[n-2] should not be used in the loop
	lc[n-2]=0
	for (i = n - 2; i >= 0; --i)
	{	x[i] = (r[i] - c[i] * x[i+1]-lc[i]*x[n-1]) / b[i];
	}
	return x;
}
function ThomasCircular(r_in,a_in,b_in,c_in,a1n, an1)
/*solves Ax=r by Guassian elimination (like the Thomas algorithm (from Wikipedia))
 	r: right-hand vector
	a: array of the sub-diagonal elements (indexes 1 till n-1)
	b: array of the diagonal elements(indexes 0 till n-1)
	c: array of the upper-diagonal elements(indexed 0 till n-2)
	a1n: the lowest most left element (position [n-1,0])
	an1: the most up most right element (position [0,n-1])
	all other elements are supposed to be 0
  |   b0 c0 0  0 ... .      .      .     a1n    |
  |   a1 b1 c1 0 ... .      .      .      0     |
  |   0  a2 b2 c2 .. .      .      .      0     |
  |   ...........    .      .      .      .     |  x = r
  |   ...........    .      .      .      .     |
  |   0 0 0 0 ... a[n-3] b[n-3] c[n-3]    0     |
  |   0 0 0 0 ...    0   a[n-2] b[n-2]  c[n-2]  |
  | an1 0 0 0 ...    0      0   a[n-1]  b[n-1]  |
*/
{
	var x,i,n,m
	var r = r_in.slice() // creates a copy
	var a = a_in.slice() // creates a copy
	var b = b_in.slice() // creates a copy
	var c = c_in.slice() // creates a copy
	n = r.length

	// lastcolumn
	// for lc, indexes 0 till n-3 are used.
	// lc[n-2] is not used, use c[n-2]
	// lc[n-1] is not used, use b[n-1]
	var lc = new Array(n)
	lc[0] = a1n

	// last row
	// lr contains a value from the last row
	// indexes 0 till n-3 are used.
	// lr[n-2] is not used, use a[n-1]
	// lr[n-1] is not used, use b[n-1]
	var lr = new Array(n)
	lr[0] = an1

	for (i = 0; i < n-3; i++)
	{
		m = a[i+1]/b[i];
		b[i+1] -= m * c[i];
		r[i+1] -= m * r[i];
		// last column // superflous when i=n-2
		lc[i+1] = -m * lc[i]

		// last row
		m = lr[i]/b[i]
		b[n-1] -= m * lc[i]
		lr[i+1] = - m * c[i] // superflous when i=n-2
		// lr[i]=0 maar deze waarde wordt niet meer gebruikt
		r[n-1] -= m * r[i]
	}
	// note that i = n-3 now
	{
		m = a[i+1]/b[i];
		b[i+1] -= m * c[i];
		r[i+1] -= m * r[i];
		// last column
		// in stead of lc[i+1]=-m*lc[i]
		c[i+1] -= m * lc [i]
		// last row
		m = lr[i]/b[i]
		b[n-1] -= m * lc[i]
		// in stead of lr[i+1]= - m * c[i] // superflous when i=n-2
		a[n-1] -= m * c[i]
		// lr[i]=0 maar deze waarde wordt niet meer gebruikt
		r[n-1]= r[n-1] - m * r[i]
	}
	i = n-2
	// in stead of: for (i = n-1; i < n; i++)
	{
		m = a[i+1]/b[i];
		// in stead of lr[i+1]= - m * c[i]
		b[i+1] -= m * c[i];
		// in stead of r[n-1]= r[n-1] - m * r[i] // reeds gedaan
		r[i+1] -= m * r[i];

		// last column
		// lc[i+1] wordt niet gebruikt, dit is b[i+1]

		// last row
		// lr[i] wordt niet gebruikt, dit is a[i+1]

		// m = lr[i]/b[i];
		// b[i+1] -=  m * c[i] // reeds gedaan
		// lr[i]=0
	}

	x= new Array(n)

	x[n-1] = r[n-1]/b[n-1];
	// the value of lc[n-2] should not be used in the loop
	lc[n-2]=0
	for (i = n - 2; i >= 0; --i)
	{	x[i] = (r[i] - c[i] * x[i+1]-lc[i]*x[n-1]) / b[i];
	}
	return x;
}
/*solves Ax=b by Guassian elimination (first converts into an n x n array))
 	r: right-hand vector
	a: array of the sub-diagonal elements (indexes 1 till n-1)
	b: array of the diagonal elements(indexes 0 till n-1)
	c: array of the upper-diagonal elements(indexed 0 till n-2)
	a1n: the lowest most left element (position [n-2,0])
	an1: the most up most right element (position [0,n-2])
	all other elements are supposed to be 0
*/
function ThomasCircularGauss(r,a,b,c,a1n, an1)
{
	var x,i,n
	n = r.length

	var A = new Array(n)
	i =0 ;
	{	A[i] = new Array(n+1).fill(0)
		//A[i][i-1] is outside the matrix
		A[i][n-1]=a1n
		A[i][i]=b[i]
		if ( 1 < n )
		{	A[i][i+1]=c[i]
		}
		A[i][n]=r[i]
	}
	for (i =1 ; i < n-1; i++)
	{	A[i] = new Array(n+1).fill(0)
		A[i][i-1]=a[i]
		A[i][i]=b[i]
		A[i][i+1]=c[i]
		A[i][n]=r[i]
	}
	if ( 1 < n )
	{	i = n-1
		A[i] = new Array(n+1).fill(0)
		A[i][i-1]=a[i]
		A[i][i]=b[i]
		// A[i][i+1] is outside the matrix
		A[i][0]=an1
		A[i][n]=r[i]
	}
	return gauss(A)
}
/*solves Ax=r by Guassian elimination (like the Thomas algorithm (from Wikipedia))
 	r: right-hand vector
	a: array of the sub-diagonal elements (indexes 1 till n-1)
	b: array of the diagonal elements(indexes 0 till n-1)
	c: array of the upper-diagonal elements(indexed 0 till n-2)
	a1n: the lowest most left element (position [n-1,0])
	an1: the most up most right element (position [0,n-1])
	all other elements are supposed to be 0
  |   b0 c0 0  0 ... .      .      .     a1n    |
  |   a1 b1 c1 0 ... .      .      .      0     |
  |   0  a2 b2 c2 .. .      .      .      0     |
  |   ...........    .      .      .      .     |  x = r
  |   ...........    .      .      .      .     |
  |   0 0 0 0 ... a[n-3] b[n-3] c[n-3]    0     |
  |   0 0 0 0 ...    0   a[n-2] b[n-2]  c[n-2]  |
  | an1 0 0 0 ...    0      0   a[n-1]  b[n-1]  |
*/

function convertThomasToGauss(r,a,b,c)
{
	var x,i,n
	n = r.length

	var A = new Array(n)
	i =0 ;
	{	A[i] = new Array(n+1).fill(0)
		//A[i][i-1] is outside the matrix
		A[i][n-1]=a[0]
		A[i][i]=b[i]
		if ( 1 < n )
		{	A[i][i+1]=c[i]
		}
		A[i][n]=r[i]
	}
	for (i =1 ; i < n-1; i++)
	{	A[i] = new Array(n+1).fill(0)
		A[i][i-1]=a[i]
		A[i][i]=b[i]
		A[i][i+1]=c[i]
		A[i][n]=r[i]
	}
	if ( 1 < n )
	{	i = n-1
		A[i] = new Array(n+1).fill(0)
		A[i][i-1]=a[i]
		A[i][i]=b[i]
		// A[i][i+1] is outside the matrix
		A[i][0]=c[i]
		A[i][n]=r[i]
	}
	return A
}
function convertThomasToGauss4(r,a,b,c,d)
{
	var x,i,n
	n = r.length

	var A = new Array(n)
	i =0 ;
	{	A[i] = new Array(n+1).fill(0)
		//A[i][i-1] is outside the matrix
		A[i][n-1]=a[0]
		A[i][i]=b[i]
		if ( 1 < n )
		{	A[i][i+1]=c[i]
			A[i][i+2]=d[i]
		}
		A[i][n]=r[i]
	}
	for (i =1 ; i < n-2; i++)
	{	A[i] = new Array(n+1).fill(0)
		A[i][i-1]=a[i]
		A[i][i]=b[i]
		A[i][i+1]=c[i]
		A[i][i+2]=d[i]
		A[i][n]=r[i]
	}
	if ( 2 < n )
	{	i = n-2
		A[i] = new Array(n+1).fill(0)
		A[i][i-1]=a[i]
		A[i][i]=b[i]
		// A[i][i+1] is outside the matrix
		A[i][i+1]=c[i]
		A[i][0]=d[i] // 0 = (i+2)%n
		A[i][n]=r[i]
	}
	if ( 1 < n )
	{	i = n-1
		A[i] = new Array(n+1).fill(0)
		A[i][i-1]=a[i]
		A[i][i]=b[i]
		// A[i][i+1] is outside the matrix
		A[i][0]=c[i] // 0 = (i+2)%n
		A[i][1]=d[i] // 1 = (i+2)%n
		A[i][n]=r[i]
	}
	return A
}
function gauss(A) {
/** Solve a linear system of equations given by a n&times;n matrix
    with a result vector n&times;1.
	copied from:
	https://martin-thoma.com/solving-linear-equations-with-gaussian-elimination/
	the vector is stored in the last colum (indexes k[n][i]
	NOTE: matrix A will be changed in this procedure.
*/

    var n = A.length;

    for (var i=0; i<n; i++) {
        // Search for maximum in this column
        var maxEl = Math.abs(A[i][i]);
        var maxRow = i;
        for(var k=i+1; k<n; k++) {
            if (Math.abs(A[k][i]) > maxEl) {
                maxEl = Math.abs(A[k][i]);
                maxRow = k;
            }
        }

        // Swap maximum row with current row (column by column)
        for (var k=i; k<n+1; k++) {
            var tmp = A[maxRow][k];
            A[maxRow][k] = A[i][k];
            A[i][k] = tmp;
        }
        // Make all rows below this one 0 in current column
        for (k=i+1; k<n; k++) {
            var c = -A[k][i]/A[i][i];
            A[k][i] = 0;
            for(var j=i+1; j<n+1; j++) {
               /* if (i==j) {
                    A[k][j] = 0;
                } else */
		{
                    A[k][j] += c * A[i][j];
                }
            }
        }
    }

    // Solve equation Ax=b for an upper triangular matrix A
    var x= new Array(n);
    for (var i=n-1; i>-1; i--) {
        x[i] = A[i][n]/A[i][i];
        for (var k=i-1; k>-1; k--) {
            A[k][n] -= A[k][i] * x[i];
        }
    }
	return x;
}

// for debugging
function matrixTranspose(A)
{
	var n = A.length

	if (0==n)
	{	return A
	}
	// else
	var m = A[0].length
	var result = new Array(m)
	for (var i=0 ; i<m ; i++)
	{
		result[i]=new Array(n)
		for (var j = 0 ; j<n ; j++ )
		{
			result[i][j]=A[j][i]
		}
	}
	return result
}
function matrixAsString(A)
{
	var result = ""
	for (i=0 ; i<A.length ; i++)
	{	for (j=0; j<A[i].length ; j++ )
		{	result += A[i][j]+" "
		}
		result +=" | \n"
	}
	return result
}
function inverseAsString(A)
{
	return  matrixAsString(computeInverse(A))
}
function computeInverse(A)
{
	return matrixTranspose(computeInverseT(A))
}
function computeInverseThomas(a,b,c,a1n, an1)
/* 	a: array of the sub-diagonal elements (indexes 1 till n-1)
	b: array of the diagonal elements(indexes 0 till n-1)
	c: array of the upper-diagonal elements(indexed 0 till n-2)
	a1n: the lowest most left element (position [n-1,0])
	an1: the most up most right element (position [0,n-1])
	all other elements are supposed to be 0
*/
{
//	r: will represent unit vectors

	var rank = b.length

	var r = new Array(rank)
	var inverse = new Array(rank)

	for (var i =0; i< rank; i++)
	{
		for (var j = 0; j< rank; j++)
		{ 	r[j]=0
		}
		r[i]=1
		inverse[i]=ThomasCircular_temp(r,a,b,c,a1n,an1)
	}
	return matrixTranspose(inverse)
}
function computeInverseT(A)
{
	var rank = A.length

	var unity = new Array(rank)
	var inverse = new Array(rank)

	for (var i =0; i< rank; i++)
	{
		for (var j = 0; j< rank; j++)
		{ 	A[j][rank]=0
		}
		A[i][rank]=1
		inverse[i]=gauss(copyMatrix(A))
	}
	return inverse
}
function matrixFill(rank,diagonal, offdiagonal)
{
	var A= new Array(rank)
	for (var i =0; i< rank; i++)
	{
		//A[i]=new Array(rank).fill(offline)
		A[i]=new Array(rank).fill(0)
		A[i][i]=diagonal
	}
	for (var i =1; i< rank; i++)
	{
		A[i][i-1]=offdiagonal
		A[i-1][i]=offdiagonal
	}
	A[rank-1][0]=offdiagonal
	A[0][rank-1]=offdiagonal
	return A
}
function copyMatrix(A)
{
	var result= [] // create a new array
	for (var i = 0; i< A.length; i++)
	{
		result[i]=A[i].slice() // creates a new array with the value sof A[i]
	}
	return result
}
function scalarTimesMatrix(s,A)
{	var result= []
	for (var i = 0; i< A.length; i++)
	{
		result[i]=A[i]
		for(var j= 0; j< A[i].length; j++)
		{	result[i][j]=A[i][j]*s
		}
	}
	return result
}